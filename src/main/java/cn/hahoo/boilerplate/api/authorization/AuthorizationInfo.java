package cn.hahoo.boilerplate.api.authorization;

public class AuthorizationInfo {
    private Integer id;

    public AuthorizationInfo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
