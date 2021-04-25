package cn.hahoo.boilerplate.api.authorization;

import cn.hahoo.boilerplate.api.user.User;
import cn.hahoo.boilerplate.api.user.UserInfo;
import cn.hahoo.boilerplate.api.user.UserService;
import cn.hahoo.boilerplate.exception.ApiException;
import cn.hahoo.boilerplate.utils.ClientUtil;
import cn.hahoo.boilerplate.utils.ValidateUtil;
import cn.hahoo.boilerplate.utils.auth.Auth;
import cn.hahoo.boilerplate.utils.auth.AuthUtil;
import cn.hahoo.boilerplate.utils.auth.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Tag(name = "授权", description = "用户登录")
@RestController
public class AuthorizationController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AuthUtil authUtil;

    @Resource
    private UserService userService;
    @Resource
    private AuthorizationService authorizationService;

    @Autowired
    public void setBCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setAuthUtil(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    /**
     * 创建授权
     */
    @Operation(summary = "登录/新建授权", description = "登录/新建授权")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(value="{" +
                                    "\"username\": \"testuser\"," +
                                    "\"password\": \"e10adc3949ba59abbe56e057f20f883e\"" +
                                    "}")
                    })
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfo.class),
                            examples = {
                                    @ExampleObject(value="{" +
                                            "\"id\": \"e2b8094e-3174-4182-a8b0-99c8a4d3d165\"," +
                                            "\"access_token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5V0xxN2VRWG9UZjhhNEFaaHh2eEhnPT0iLCJpYXQiOiIxNDgwNjk3MzgyIiwiZXhwIjoiMTQ4MDcwNDU4MiIsImp0aSI6ImQwZWU3YjBlLTFhYWQtNGM5MS05ZTlkLWUzNGY5NjQzYTk0NyIsInNjb3BlcyI6WyJST0xFX01FTUJFUiJdfQ.S4myHmBzl0wljqdZc9yWmlAbuW44MX6XrhB7b934rw\"," +
                                            "\"expires_in\": 7200," +
                                            "\"refresh_token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5V0xxN2VRWG9UZjhhNEFaaHh2eEhnPT0iLCJpYXQiOiIxNDgwNjk3MzgyIiwianRpIjoiZThjY2VlNmMtMWZjZS00MGMwLThhNWUtOGQ0NzM1MDJiYjRiIiwic2NvcGVzIjpbIlJPTEVfUkVGUkVTSF9UT0tFTiJdfQ.UPsGhIYwo7riyPtV6GYHVRFLsENHX4OmTo0LaD0F0h82M9VrN5v3QDfGxTi1ZbfS6M8dnJaRQD2i2u3A\"," +
                                            "\"updated_at\": \"2011-09-06T20:39:23Z\"," +
                                            "\"created_at\": \"2011-09-06T20:39:23Z\"" +
                                            "}")
                            })
            )
    })

    @PostMapping("/authorizations")
    public Map<String, Object> createAuth(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        ValidateUtil.required(body.get("username"), "用户名");
        ValidateUtil.stringType(body.get("username"), "用户名");

        ValidateUtil.required(body.get("password"), "密码");
        ValidateUtil.stringType(body.get("password"), "密码");

        String username = (String)body.get("username");
        String password = (String)body.get("password");

        Map<String, String> client = ClientUtil.getClientInfo(request);

        User user = userService.getByUsername(username);
        if (user == null) {
            authorizationService.insertLog(1003, username, client.get("userAgent"), 0, 0, client.get("ip"));
            throw new ApiException(100400, "帐号或密码不正确", 422);
        }

        // 验证密码
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            authorizationService.insertLog(1001, "", client.get("userAgent"), user.getId(), 0, client.get("ip"));
            throw new ApiException(100400, "帐号或密码不正确", 422);
        }

        // 用户被删除
        if (user.getIsDel() != 0) {
            authorizationService.insertLog(1004, "", client.get("userAgent"), user.getId(), 0, client.get("ip"));
            throw new ApiException(100400, "帐号或密码不正确", 422);
        }

        // 用户被禁用
        if (user.getIsEnabled() !=1) {
            authorizationService.insertLog(1002, "", client.get("userAgent"), user.getId(), 0, client.get("ip"));
            throw new ApiException(100400, "帐号或密码不正确", 422);
        }

        Auth auth = authUtil.createAuth(user.getId(), client);
        authorizationService.insertLog(1, "", client.get("userAgent"), user.getId(), auth.getAuthID(), client.get("ip"));

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", auth.getRefreshTokenId());
        map.put("access_token", auth.getAccessToken().getToken());
        map.put("expires_in", auth.getAccessToken().getExpire());
        map.put("refresh_token", auth.getRefreshToken().getToken());
        map.put("created_at", auth.getAccessToken().getCreateTime());
        map.put("updated_at", auth.getAccessToken().getCreateTime());

        return map;
    }

    /**
     * 刷新授权
     */
    @Operation(summary = "刷新授权", description = "刷新授权", security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfo.class),
                            examples = {
                                    @ExampleObject(value="{" +
                                            "\"id\": \"e2b8094e-3174-4182-a8b0-99c8a4d3d165\"," +
                                            "\"access_token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5V0xxN2VRWG9UZjhhNEFaaHh2eEhnPT0iLCJpYXQiOiIxNDgwNjk3MzgyIiwiZXhwIjoiMTQ4MDcwNDU4MiIsImp0aSI6ImQwZWU3YjBlLTFhYWQtNGM5MS05ZTlkLWUzNGY5NjQzYTk0NyIsInNjb3BlcyI6WyJST0xFX01FTUJFUiJdfQ.S4myHmBzl0wljqdZc9yWmlAbuW44MX6XrhB7b934rw\"," +
                                            "\"expires_in\": 7200," +
                                            "\"refresh_token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5V0xxN2VRWG9UZjhhNEFaaHh2eEhnPT0iLCJpYXQiOiIxNDgwNjk3MzgyIiwianRpIjoiZThjY2VlNmMtMWZjZS00MGMwLThhNWUtOGQ0NzM1MDJiYjRiIiwic2NvcGVzIjpbIlJPTEVfUkVGUkVTSF9UT0tFTiJdfQ.UPsGhIYwo7riyPtV6GYHVRFLsENHX4OmTo0LaD0F0h82M9VrN5v3QDfGxTi1ZbfS6M8dnJaRQD2i2u3A\"," +
                                            "\"updated_at\": \"2011-09-06T20:39:23Z\"," +
                                            "\"created_at\": \"2011-09-06T20:39:23Z\"" +
                                            "}")
                            })
            )
    })

    @PutMapping("/authorizations/{id}")
    public Map<String, Object> refreshAuth(@PathVariable(name = "id") String id, HttpServletRequest request) {
        ValidateUtil.uuid(id, "授权id", true);

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer")) throw new ApiException(100403, "Authentication failure", 401);

        Map<String, String> client = ClientUtil.getClientInfo(request);

        String jwtToken = token.replace("Bearer","");
        if (jwtToken.isEmpty()) {
            authorizationService.insertLog(1051, "", client.get("userAgent"), 0, 0, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }

        Jws<Claims> jws;
        try {
            jws = authUtil.parseToken(jwtToken);
        } catch (JwtException ex) {
            throw new ApiException(100403, "Authentication failure", 401);
        }

        // 检查权限
        boolean havePermission = false;

        List<String> scopes = authUtil.getScopes(jws.getBody().get("scopes"));

        for (String scope: scopes) {
            if (scope.equals("ROLE_REFRESH_TOKEN")) {
                havePermission = true;
                break;
            }
        }
        if (!havePermission) {
            authorizationService.insertLog(1053, "", client.get("userAgent"), 0, 0, client.get("ip"));
            throw new ApiException(100404, "No permission", 403);
        }


        Integer authId = Integer.parseInt(jws.getBody().getSubject());

        Authorization authData = authorizationService.getById(authId);
        if (authData == null) {
            authorizationService.insertLog(1058, "", client.get("userAgent"), 0, authId, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }

        Integer userId = authData.getUserId();
        if (!authData.getUuid().equals(id)) {
            authorizationService.insertLog(1059, "", client.get("userAgent"), userId, authId, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }
        if (authData.getIsEnabled() != 1) {
            authorizationService.insertLog(1060, "", client.get("userAgent"), userId, authId, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }
        if (!authData.getRefreshToken().equals(jws.getBody().getId()) ) {
            authorizationService.insertLog(1063, "", client.get("userAgent"), userId, authId, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }

        String refreshTokenJti = UUID.randomUUID().toString();
        Date updateTime = new Date();

        Token accessToken = authUtil.createAccessToken(userId);
        Token refreshToken = authUtil.createRefreshToken(authId, refreshTokenJti);

        AuthorizationBlacklist authorizationBlacklist = new AuthorizationBlacklist();
        authorizationBlacklist.setAccessTokenId(authData.getAccessTokenId());
        authorizationBlacklist.setAccessTokenExp(authData.getAccessTokenExp());
        authorizationBlacklist.setUserId(userId);

        authorizationService.addBlacklist(authorizationBlacklist);

        Authorization authorization = new Authorization();
        authorization.setId(authId);
        authorization.setRefreshToken(refreshTokenJti);
        authorization.setLastRefreshTime(updateTime);
        authorization.setAccessTokenId(accessToken.getJti());
        authorization.setAccessTokenExp(accessToken.getExpireTime());
        authorization.setAccessTokenIat(accessToken.getCreateTime());
        authorization.setUpdateTime(updateTime);

        authorizationService.updateAuth(authorization);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("access_token", accessToken.getToken());
        map.put("expires_in", accessToken.getExpire());
        map.put("refresh_token", refreshToken.getToken());
        map.put("created_at", authData.getCreateTime());
        map.put("updated_at", updateTime);

        return map;
    }

    /**
     * 删除授权
     */
    @Operation(summary = "注销/删除授权", description = "注销/删除授权", security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "successful operation"
            )
    })

    @DeleteMapping("/authorizations/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAuth(@PathVariable(name = "id") String id, HttpServletRequest request) {
        ValidateUtil.uuid(id, "授权id", true);
        Map<String, String> client = ClientUtil.getClientInfo(request);

        Authorization authData = authorizationService.getByUuid(id);
        if (authData == null) {
            authorizationService.insertLog(1101, "", client.get("userAgent"), 0, 0, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }

        Integer userId = authData.getUserId();
        Integer authId = authData.getId();

        if (authData.getIsEnabled() != 1) {
            authorizationService.insertLog(1102, "", client.get("userAgent"), userId, authId, client.get("ip"));
            throw new ApiException(100403, "Authentication failure", 401);
        }

        authorizationService.revokeAuth(authId);

        AuthorizationBlacklist authorizationBlacklist = new AuthorizationBlacklist();
        authorizationBlacklist.setAccessTokenId(authData.getAccessTokenId());
        authorizationBlacklist.setAccessTokenExp(authData.getAccessTokenExp());
        authorizationBlacklist.setUserId(userId);

        authorizationService.addBlacklist(authorizationBlacklist);

        authorizationService.insertLog(3, "", client.get("userAgent"), userId, authId, client.get("ip"));
    }
}
