package cn.hahoo.boilerplate.api.user;

import cn.hahoo.boilerplate.api.authorization.AuthorizationInfo;
import cn.hahoo.boilerplate.api.authorization.AuthorizationService;
import cn.hahoo.boilerplate.exception.ApiException;
import cn.hahoo.boilerplate.utils.ClientUtil;
import cn.hahoo.boilerplate.utils.ValidateUtil;
import cn.hahoo.boilerplate.utils.auth.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

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

    @GetMapping(value="/user")
    public UserInfo getInfo(){
        AuthorizationInfo authInfo = authUtil.getAuthInfo();

        return userService.getUserInfoById(authInfo.getId());
    }

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
