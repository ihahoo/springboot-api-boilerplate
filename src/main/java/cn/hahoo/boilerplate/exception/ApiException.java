package cn.hahoo.boilerplate.exception;

public class ApiException extends RuntimeException {
    private Integer errCode;
    private String errMsg;
    private Integer status;

    public ApiException() {
    }

    public ApiException(Integer errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.status = 400;
    }

    public ApiException(Integer errCode, String errMsg, Integer status) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.status = status;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
