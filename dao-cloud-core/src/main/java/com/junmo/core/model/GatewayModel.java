package com.junmo.core.model;


import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/3 23:54
 * @description: Gateway Model
 */
@Data
public class GatewayModel extends Model {

    private ProxyProviderModel proxyProviderModel;

    private LimitModel limitModel;

    public GatewayModel(ProxyProviderModel proxyProviderModel, LimitModel limitModel) {
        this.proxyProviderModel = proxyProviderModel;
        this.limitModel = limitModel;
    }
}
