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
    // B级别 系统错误,NPE
    /**
     * 系统内部报错,B一级宏观错误
     * 后端确定不了的，选这个
     */
    SYSTEM_ERROR("B0001", "系统内部错误"),
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
