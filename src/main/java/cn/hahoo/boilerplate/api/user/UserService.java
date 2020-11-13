package cn.hahoo.boilerplate.api.user;

import java.util.Date;
import java.util.List;

public interface UserService {
    List<User> getAll();
    User getById(Integer id);
    User getByUsername(String username);
    void insert(User user);
    void update(User user);
    void delete(Integer id);
    void updateLastLogin(Integer userId, Date lastLoginTime, String lastLoginIp);
    UserInfo getUserInfoById(Integer id);
}
