package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/1/26 10:44
 * @description: 网关请求模型
 */
@Data
public class GatewayRequestModel extends ServiceRequestModel {

    /**
     * http 请求信息
     */
    private DaoCloudServletRequest request;


    public GatewayRequestModel(String provider, int version, String methodName, DaoCloudServletRequest request) {
        this.provider = provider;
        this.version = version;
        this.methodName = methodName;
        this.request = request;
    }
}
