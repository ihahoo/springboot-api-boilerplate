package cn.hahoo.boilerplate.api.authorization;

public interface AuthorizationService {

    /**
     * 添加日志
     */
    void insertLog(AuthorizationLog log);

    /**
     * 添加日志
     */
    void insertLog(Integer logType, String msg, String userAgent, Integer userId, Integer authId, String ip);

    /**
     * 将用户登录的token加入黑名单
     */
    void addBlacklist(AuthorizationBlacklist authorizationBlacklist);

    /**
     * 检查id是否在黑名单中
     */
    boolean isInBlacklist(String id);

    /**
     * 创建授权
     */
    Integer createAuth(Authorization authorization, String ip);

    /**
     * 撤销授权
     */
    void revokeAuth(Integer id);

    /**
     * 通过id获取授权信息
     */
    Authorization getById(Integer id);

    /**
     * 通过uuid获取授权信息
     */
    Authorization getByUuid(String uuid);

    /**
     * 更新授权
     */
    void updateAuth(Authorization authorization);
}
