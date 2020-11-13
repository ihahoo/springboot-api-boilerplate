package cn.hahoo.boilerplate.api.authorization;

import java.io.Serializable;
import java.util.Date;

public class Authorization implements Serializable {

    private Integer id;
    private Integer userId;
    private String uuid;
    private Integer clientType;
    private String refreshToken;
    private Date createTime;
    private Date updateTime;
    private Date lastRefreshTime;
    private String accessTokenId;
    private Date accessTokenExp;
    private Date accessTokenIat;
    private Integer isEnabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
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

    public Date getAccessTokenIat() {
        return accessTokenIat;
    }

    public void setAccessTokenIat(Date accessTokenIat) {
        this.accessTokenIat = accessTokenIat;
    }

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return "Authorization{" +
                "id=" + id +
                ", userId=" + userId +
                ", uuid='" + uuid + '\'' +
                ", clientType=" + clientType +
                ", refreshToken='" + refreshToken + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", lastRefreshTime=" + lastRefreshTime +
                ", accessTokenId='" + accessTokenId + '\'' +
                ", accessTokenExp=" + accessTokenExp +
                ", accessTokenIat=" + accessTokenIat +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
