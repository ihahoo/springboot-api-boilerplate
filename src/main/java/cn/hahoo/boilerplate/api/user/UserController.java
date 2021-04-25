package cn.hahoo.boilerplate.api.user;

import cn.hahoo.boilerplate.api.authorization.AuthorizationInfo;
import cn.hahoo.boilerplate.api.authorization.AuthorizationService;
import cn.hahoo.boilerplate.exception.ApiException;
import cn.hahoo.boilerplate.utils.ClientUtil;
import cn.hahoo.boilerplate.utils.ValidateUtil;
import cn.hahoo.boilerplate.utils.auth.AuthUtil;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Tag(name = "用户", description = "已登录用户操作")
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private AuthorizationService authorizationService;

    private AuthUtil authUtil;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public void setAuthUtil(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @Autowired
    public void setBCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Operation(summary = "获取个人资料", description = "获取个人资料", security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfo.class),
                            examples = {
                                    @ExampleObject(value="{" +
                                            "\"id\": \"3fe8d31f-8d53-4be0-b0d8-2a739fde86b5\"," +
                                            "\"username\": \"ray\"," +
                                            "\"last_login_at\": \"2011-09-06T20:39:23Z\"," +
                                            "\"last_login_ip\": \"192.168.0.222\"" +
                                            "}")
                            })
            )
    })

    @GetMapping(value="/user")
    public UserInfo getInfo(){
        AuthorizationInfo authInfo = authUtil.getAuthInfo();

        UserInfo userInfo = userService.getUserInfoById(authInfo.getId());
        if (userInfo == null) {
            throw new ApiException(400007, "无法获得用户信息", 422);
        }
        return userInfo;
    }

    @Operation(summary = "修改个人密码", description = "修改个人密码", security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(value="{" +
                                    "\"old_password\": \"3fe8d31f\"," +
                                    "\"new_password\": \"nntr7zbjmde\"," +
                                    "\"confirm_password\": \"confirm_password\"" +
                                    "}")
                    })
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation"
            )
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value="/user/password")
    @ResponseStatus(value = HttpStatus.OK)
    public void changePassword(@RequestBody Map<String, Object> body, HttpServletRequest request){
        AuthorizationInfo authInfo = authUtil.getAuthInfo();

        ValidateUtil.required(body.get("old_password"), "原密码");
        ValidateUtil.stringType(body.get("old_password"), "原密码");

        ValidateUtil.required(body.get("new_password"), "新密码");
        ValidateUtil.stringType(body.get("new_password"), "新密码");

        ValidateUtil.required(body.get("confirm_password"), "确认密码");
        ValidateUtil.stringType(body.get("confirm_password"), "确认密码");

        String oldPassword = (String)body.get("old_password");
        String newPassword = (String)body.get("new_password");
        String confirmPassword = (String)body.get("confirm_password");

        if (!newPassword.equals(confirmPassword)) {
            throw new ApiException(100301, "新密码和确认密码不一致", 422);
        }

        if (newPassword.length() < 6) {
            throw new ApiException(100301, "新密码长度不少小于6位", 422);
        }

        Map<String, String> client = ClientUtil.getClientInfo(request);

        User user = userService.getById(authInfo.getId());
        if (user == null) {
            throw new ApiException(400007, "无法获得用户信息", 422);
        }

        // 用户被删除
        if (user.getIsDel() != 0) {
            throw new ApiException(100400, "帐号或密码不正确", 422);
        }

        // 用户被禁用
        if (user.getIsEnabled() !=1) {
            throw new ApiException(100400, "帐号或密码不正确", 422);
        }

        // 验证密码
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ApiException(100407, "原密码错误", 422);
        }

        User newUser = new User();
        newUser.setId(authInfo.getId());
        newUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        newUser.setUpdateTime(new Date());

        userService.update(newUser);
        authorizationService.insertLog(5, "", client.get("userAgent"), user.getId(), 0, client.get("ip"));
    }
}
