package com.junmo.core.model;

import java.util.Map;
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
    private HttpServletRequestModel request;


    public GatewayRequestModel(String provider, int version, String methodName, HttpServletRequestModel request) {
        this.provider = provider;
        this.version = version;
        this.methodName = methodName;
        this.request = request;
    }
}
