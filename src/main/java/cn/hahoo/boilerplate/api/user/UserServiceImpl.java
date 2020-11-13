package cn.hahoo.boilerplate.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service()
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAll() {
        return userMapper.getAll();
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void delete(Integer id) {
        userMapper.delete(id);
    }

    @Override
    public void updateLastLogin(Integer userId, Date lastLoginTime, String lastLoginIp) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(lastLoginTime);
        user.setLastLoginIp(lastLoginIp);
        userMapper.updateLastLogin(user);
    }

    @Override
    public UserInfo getUserInfoById(Integer id) {
        return userMapper.getUserInfoById(id);
    }
}
