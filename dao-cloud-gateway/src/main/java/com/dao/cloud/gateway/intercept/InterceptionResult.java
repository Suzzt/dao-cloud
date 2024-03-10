package com.dao.cloud.gateway.intercept;

/**
 * @author: sucf
 * @date: 2024/3/10 12:06
 * @description: 网关拦截返回的结果封装
 */
public class InterceptionResult {

    /**
     * 是否拦截
     */
    private Boolean success;

    /**
     * 信息
     */
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InterceptionResult(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public InterceptionResult(Boolean success) {
        this.success = success;
    }

    public static InterceptionResult success() {
        return new InterceptionResult(true);
    }
}
