package cn.hahoo.boilerplate.utils.auth;

import cn.hahoo.boilerplate.api.authorization.Authorization;
import cn.hahoo.boilerplate.api.authorization.AuthorizationInfo;
import cn.hahoo.boilerplate.api.authorization.AuthorizationService;
import cn.hahoo.boilerplate.exception.ApiException;
import cn.hahoo.boilerplate.utils.AesUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class AuthUtil {

    @Value("${jwt.jwtKey}")
    private String jwtKey;

    @Value("${jwt.aesKey}")
    private String aesKey;

    @Value("${jwt.access-token-expire}")
    private Integer accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private Integer refreshTokenExpire;

    @Resource
    private AuthorizationService authorizationService;

    /**
     * 创建授权
     */
    public Auth createAuth(Integer userId, Map<String, String> client) {
        Token accessToken = createAccessToken(userId);

        String refreshTokenId = UUID.randomUUID().toString();
        String refreshTokenJti = UUID.randomUUID().toString();

        Authorization authorization = new Authorization();
        authorization.setUserId(userId);
        authorization.setUuid(refreshTokenId);
        authorization.setClientType(Integer.parseInt(client.get("clientType")));
        authorization.setRefreshToken(refreshTokenJti);
        authorization.setCreateTime(new Date());
        authorization.setAccessTokenId(accessToken.getJti());
        authorization.setAccessTokenExp(accessToken.getExpireTime());
        authorization.setAccessTokenIat(accessToken.getCreateTime());
        authorization.setIsEnabled(1);

        Integer authorizationId = authorizationService.createAuth(authorization, client.get("ip"));

        Token refreshToken = createRefreshToken(authorizationId, refreshTokenJti);

        Auth auth = new Auth();
        auth.setAccessToken(accessToken);
        auth.setRefreshToken(refreshToken);
        auth.setRefreshTokenId(refreshTokenId);
        auth.setAuthID(authorizationId);

        return auth;
    }

    /**
     * 加密subject
     */
    public String encryptSubject(String sub) {
        return AesUtil.encrypt(sub, aesKey);
    }

    /**
     * 生成access_token
     */
    public Token createAccessToken(Integer userId) {
        String[] scopes= {"ROLE_ADMIN"};
        Date createTime = new Date(System.currentTimeMillis());
        Date expireTime = new Date(System.currentTimeMillis() + accessTokenExpire * 1000);
        String jti = UUID.randomUUID().toString();
        String sub = encryptSubject(userId.toString());

        String accessToken = Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(createTime)
                .setExpiration(expireTime)
                .setId(jti)
                .claim("scopes", scopes)
                .signWith(Keys.hmacShaKeyFor(jwtKey.getBytes()), SignatureAlgorithm.HS512)
                .compact();

        Token token = new Token();
        token.setToken(accessToken);
        token.setExpireTime(expireTime);
        token.setCreateTime(createTime);
        token.setExpire(accessTokenExpire);
        token.setJti(jti);

        return token;
    }

    /**
     * 生成refresh_token
     */
    public Token createRefreshToken(Integer authorizationId, String refreshTokenJti) {
        String[] scopes= {"ROLE_REFRESH_TOKEN"};
        Date createTime = new Date(System.currentTimeMillis());
        Date expireTime = new Date(System.currentTimeMillis() + refreshTokenExpire * 1000);
        String jti = refreshTokenJti == null ? UUID.randomUUID().toString() : refreshTokenJti;
        String sub = encryptSubject(authorizationId.toString());

        String refreshToken = Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(createTime)
                .setExpiration(expireTime)
                .setId(jti)
                .claim("scopes", scopes)
                .signWith(Keys.hmacShaKeyFor(jwtKey.getBytes()), SignatureAlgorithm.HS512)
                .compact();

        Token token = new Token();
        token.setToken(refreshToken);
        token.setExpireTime(expireTime);
        token.setCreateTime(createTime);
        token.setExpire(refreshTokenExpire);
        token.setJti(jti);

        return token;
    }

    /**
     * 解析令牌
     */
    public Jws<Claims> parseToken(String jwtToken) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(jwtKey.getBytes())
                .build()
                .parseClaimsJws(jwtToken);


        String sub = jws.getBody().getSubject();
        String decryptSub = AesUtil.decrypt(sub, aesKey);
        jws.getBody().setSubject(decryptSub);

        return jws;
    }

    /**
     * 解析权限
     */
    public List<String> getScopes(Object scopes) {
        List<String> roles = new ArrayList<>();
        if (scopes instanceof ArrayList<?>) {
            for (Object o: (List<?>) scopes) {
                roles.add((String) o);
            }
        }

        return roles;
    }

    /**
     * 获取认证授权等信息
     */
    public AuthorizationInfo getAuthorizationInfo () {

        return (AuthorizationInfo) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    /**
     * 获取认证授权等信息(带错误检测)
     */
    public AuthorizationInfo getAuthInfo () {

        AuthorizationInfo authInfo = getAuthorizationInfo();

        if (authInfo == null || authInfo.getId() == null || authInfo.getId() <= 0)
            throw new ApiException(100403, "Authentication failure", 401);

        return authInfo;
    }
}
