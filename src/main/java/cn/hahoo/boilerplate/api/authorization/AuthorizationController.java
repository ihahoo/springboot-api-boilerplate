package cn.hahoo.boilerplate.api.authorization;

import cn.hahoo.boilerplate.api.user.User;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
