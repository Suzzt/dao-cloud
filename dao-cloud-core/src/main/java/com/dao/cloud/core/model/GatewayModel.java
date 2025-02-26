package com.dao.cloud.core.model;


import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * Gateway Model
 */
@Data
public class GatewayModel extends Model {

    private ProxyProviderModel proxyProviderModel;

    private GatewayConfigModel gatewayConfigModel;

    public GatewayModel(ProxyProviderModel proxyProviderModel, GatewayConfigModel gatewayConfigModel) {
        this.proxyProviderModel = proxyProviderModel;
        this.gatewayConfigModel = gatewayConfigModel;
    }
}
