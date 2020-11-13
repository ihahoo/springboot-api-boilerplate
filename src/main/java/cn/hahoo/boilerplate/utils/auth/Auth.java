package cn.hahoo.boilerplate.utils.auth;

public class Auth {

    private Token accessToken;
    private Token refreshToken;
    private String refreshTokenId;
    private Integer authID;

    public Token getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Token accessToken) {
        this.accessToken = accessToken;
    }

    public Token getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(Token refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    public void setRefreshTokenId(String refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
    }

    public Integer getAuthID() {
        return authID;
    }

    public void setAuthID(Integer authID) {
        this.authID = authID;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "accessToken=" + accessToken +
                ", refreshToken=" + refreshToken +
                ", refreshTokenId='" + refreshTokenId + '\'' +
                ", authID=" + authID +
                '}';
    }
}
