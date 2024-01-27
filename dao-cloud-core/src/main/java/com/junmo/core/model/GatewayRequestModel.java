package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/1/26 10:44
 * @description: 网关请求模型
 */
@Data
public class GatewayRequestModel extends Model {

    /**
     * 序列id
     */
    private long sequenceId;

    /**
     * 调用接口名，在服务端找到它对应的实现
     */
    private String provider;

    /**
     * 版本
     */
    private int version;

    /**
     * 调用接口中方法名
     */
    private String methodName;

    /**
     * 请求参数(json)
     */
    private String params;

    /**
     * 请求header(json)
     */
    private String header;
}
