package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/1/26 10:44
 * @description: 网关请求模型
 */
@Data
public class GatewayRequestModel extends ServiceRequestModel {

    /**
     * 请求参数(json)
     */
    private String params;

    /**
     * 请求header(json)
     */
    private String header;

    public GatewayRequestModel(String provider, int version, String methodName, String params, String header) {
        this.provider = provider;
        this.version = version;
        this.methodName = methodName;
        this.params = params;
        this.header = header;
    }
}
