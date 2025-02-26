package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * Synchronization of gateway data information between clusters
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
