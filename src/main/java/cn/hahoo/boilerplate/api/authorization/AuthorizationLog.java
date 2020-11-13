package cn.hahoo.boilerplate.api.authorization;

import java.io.Serializable;
import java.util.Date;

public class AuthorizationLog implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer logType;
    private String ip;
    private Date logTime;
    private Integer clientType;
    private Integer authId;
    private String userAgent;
    private String log;
    private String clientVersion;

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

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public Integer getAuthId() {
        return authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override
    public String toString() {
        return "AuthorizationLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", logType=" + logType +
                ", ip='" + ip + '\'' +
                ", logTime=" + logTime +
                ", clientType=" + clientType +
                ", authId=" + authId +
                ", userAgent='" + userAgent + '\'' +
                ", log='" + log + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
