package cn.hahoo.boilerplate.api.user;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    List<User> getAll();
    User getById(Integer id);
    User getByUsername(String username);
    void insert(User user);
    void update(User user);
    void delete(Integer id);
    void updateLastLogin(User user);
    UserInfo getUserInfoById(Integer id);
}
