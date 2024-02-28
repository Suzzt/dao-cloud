package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/27 22:47
 * @description: Synchronization of gateway data information between clusters
 */
@Data
public class GatewayShareClusterRequestModel extends AbstractShareClusterRequestModel {
    /**
     * service information key
     */
    private ProxyProviderModel proxyProviderModel;

    /**
     * gateway configuration information
     */
    private GatewayConfigModel gatewayConfigModel;
}
