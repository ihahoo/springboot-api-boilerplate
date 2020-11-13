package cn.hahoo.boilerplate.api.authorization;

import java.io.Serializable;
import java.util.Date;

public class AuthorizationBlacklist implements Serializable {

    private Integer id;
    private String accessTokenId;
    private Date accessTokenExp;
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(String accessTokenId) {
        this.accessTokenId = accessTokenId;
    }

    public Date getAccessTokenExp() {
        return accessTokenExp;
    }

    public void setAccessTokenExp(Date accessTokenExp) {
        this.accessTokenExp = accessTokenExp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AuthorizationBlacklist{" +
                "id=" + id +
                ", accessTokenId='" + accessTokenId + '\'' +
                ", accessTokenExp=" + accessTokenExp +
                ", userId=" + userId +
                '}';
    }
}
