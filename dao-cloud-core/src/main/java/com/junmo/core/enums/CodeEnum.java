package com.junmo.core.enums;

/**
 * @author: sucf
 * @create: 2020-10-16 11:22:12
 * @description: result返回值code对照码
 */
public enum CodeEnum {

    /**
     * 请求成功
     */
    SUCCESS("00000", "请求成功"),

    // A级别 前端用户端错误
    /**
     * 参数错误,A一级宏观错误
     */
    PARAMETER_ERROR("A0001", "参数错误"),

    /**
     * 入参异常报错,A二级宏观错误
     */
    PARAMETER_EXCEPTION_ERROR("A1000", "参数数据异常"),

    /**
     * 账号密码错误
     */
    PARAMETER_ACCOUNT_ERROR("A1001", "账号密码错误"),

    // D级别 dao-cloud服务交互侧
    /**
     * dao-cloud内部未知错误
     */
    SERVICE_UNKNOWN_ERROR("D0001", "服务未知错误"),

    // G级别 网关侧
    /**
     * 网关请求失败
     */
    GATEWAY_REQUEST_SUCCESS("G0000", "网关请求成功"),

    /**
     * 网关请求失败
     */
    GATEWAY_REQUEST_ERROR("G0001", "网关请求失败"),

    /**
     * 网关请求超时
     */
    GATEWAY_REQUEST_TIMEOUT("G0002", "网关请求超时"),

    /**
     * 网关请求被限流
     */
    GATEWAY_REQUEST_LIMIT("G0003", "网关请求被限流"),

    /**
     * 网关请求参数缺失
     */
    GATEWAY_REQUEST_PARAM_DELETION("G0004", "网关请求必要参数缺失"),

    /**
     * 服务接口不存在
     */
    GATEWAY_SERVICE_NOT_EXIST("G0004", "服务接口不存在"),
    ;
    private String code;

    private String text;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    CodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
