package com.dao.cloud.core.model;

import lombok.Data;


/**
 * @author: sucf
 * @date: 2024/7/11 16:01
 * @description: Synchronous interface call data
 */
@Data
public class CallTrendModel extends Model {

    /**
     * Service information
     */
    private ProxyProviderModel proxyProviderModel;

    /**
     * Method name
     */
    private String methodName;

    /**
     * Call count
     */
    private Long count;

    public CallTrendModel(ProxyProviderModel proxyProviderModel, String methodName, Long count) {
        this.proxyProviderModel = proxyProviderModel;
        this.methodName = methodName;
        this.count = count;
    }
}
