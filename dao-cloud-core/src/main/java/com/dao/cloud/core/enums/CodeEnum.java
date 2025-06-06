package com.dao.cloud.core.enums;

/**
 * result返回值code对照码
 *
 * @author sucf
 * @since 1.0.0
 * @date 2020-10-16 11:22:12
 * @since 1.0.0
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

    /**
     * 同步数据处理不了意外的类型
     */
    SYNC_DATA_EXTRA_TYPE_ERROR("D0002", "同步数据处理不了意外的类型"),

    /**
     * 集群间同步数据发生了错误
     */
    SYNC_SHARE_CLUSTER_DATA_ERROR("D0003", "同步数据发生了错误"),

    /**
     * 拉取节点失败
     */
    PULL_SERVICE_NODE_ERROR("D0004", "拉取服务节点失败"),

    /**
     * 拉取服务配置失败
     */
    PULL_SERVER_CONFIG_ERROR("D0005", "拉取服务配置失败"),

    /**
     * 拉取网关配置失败
     */
    PULL_GATEWAY_CONFIG_ERROR("D0006", "拉取网关配置失败"),

    /**
     * 拉取接口调用数据失败
     */
    PULL_CALL_TREND_ERROR("D0007", "拉取接口调用趋势失败"),

    /**
     * 日志配置地址不存在
     */
    COLLECTION_LOG_NOT_EXIST("D0100", "集合日志不存在"),

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
     * 网关服务接口不存在
     */
    GATEWAY_SERVICE_NOT_EXIST("G0005", "网关服务接口不存在"),

    /**
     * 网关参数处理绑定失败
     */
    GATEWAY_PARAM_PROCESS_BINDING_FAILED("G0006", "网关参数处理绑定失败"),

    /**
     * 网关拦截不通过
     */
    GATEWAY_INTERCEPTION_FAIL("G0008", "网关拦截不通过"),

    // S级别 service
    /**
     * 服务提供者不存在
     */
    SERVICE_PROVIDER_NOT_EXIST("S0001", "服务提供者不存在"),

    /**
     * 服务提供者方法(函数)不存在
     */
    SERVICE_PROVIDER_METHOD_NOT_EXIST("S0002", "服务提供者方法(函数)不存在"),

    /**
     * 服务调用失败
     */
    SERVICE_INVOKE_ERROR("S0003", "服务调用失败"),
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
