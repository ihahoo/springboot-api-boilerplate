package cn.hahoo.boilerplate.api.authorization;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuthorizationMapper {

    void insertLog(AuthorizationLog log);
    void insertBlacklist(AuthorizationBlacklist authorizationBlacklist);
    void insertAuth(Authorization authorization);
    void disableAuth(Integer id);
    Authorization getById(Integer id);
    Authorization getByUuid(String uuid);
    void updateAuth(Authorization authorization);
}
