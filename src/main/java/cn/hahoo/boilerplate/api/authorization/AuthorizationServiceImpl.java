package cn.hahoo.boilerplate.api.authorization;

import cn.hahoo.boilerplate.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service()
public class AuthorizationServiceImpl implements AuthorizationService {

    private AuthorizationMapper authorizationMapper;

    @Resource
    private UserService userService;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setAuthorizationMapper(AuthorizationMapper authorizationMapper) {
        this.authorizationMapper = authorizationMapper;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void insertLog(AuthorizationLog log) {
        authorizationMapper.insertLog(log);
    }

    @Override
    public void insertLog(Integer logType, String msg, String userAgent, Integer userId, Integer authId, String ip) {
        AuthorizationLog log = new AuthorizationLog();
        log.setUserId(userId);
        log.setLogType(logType);
        log.setIp(ip);
        log.setLogTime(new Date());
        log.setClientType(10);
        log.setAuthId(authId);
        log.setUserAgent(userAgent);
        log.setLog(msg);

        authorizationMapper.insertLog(log);
    }

    @Override
    public void addBlacklist(AuthorizationBlacklist authorizationBlacklist) {
        Date now = new Date();
        Date exp = authorizationBlacklist.getAccessTokenExp();

        authorizationMapper.insertBlacklist(authorizationBlacklist);

        if (exp.getTime() > now.getTime()) {
            stringRedisTemplate.opsForValue().set(authorizationBlacklist.getAccessTokenId(),
                    authorizationBlacklist.getUserId().toString(), exp.getTime() - now.getTime(), TimeUnit.MILLISECONDS);

        }
    }

    @Override
    public boolean isInBlacklist(String id) {
        return stringRedisTemplate.hasKey(id);
    }

    @Override
    public Integer createAuth(Authorization authorization, String ip) {
        authorizationMapper.insertAuth(authorization);
        userService.updateLastLogin(authorization.getUserId(), new Date(), ip);
        return authorization.getId();
    }

    @Override
    public void revokeAuth(Integer id) {
        authorizationMapper.disableAuth(id);
    }

    @Override
    public Authorization getById(Integer id) {
        return authorizationMapper.getById(id);
    }

    @Override
    public Authorization getByUuid(String uuid) {
        return authorizationMapper.getByUuid(uuid);
    }

    @Override
    public void updateAuth(Authorization authorization) {
        authorizationMapper.updateAuth(authorization);
    }
}
