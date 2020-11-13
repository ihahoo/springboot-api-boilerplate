package cn.hahoo.boilerplate.utils.auth;

import java.util.Date;

public class Token {

    private String token;
    private Date expireTime;
    private Date createTime;
    private Integer expire;
    private String jti;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expireTime=" + expireTime +
                ", createTime=" + createTime +
                ", expire=" + expire +
                ", jti='" + jti + '\'' +
                '}';
    }
}
