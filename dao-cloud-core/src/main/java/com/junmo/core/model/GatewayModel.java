package com.junmo.core.model;


import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/3 23:54
 * @description:
 */
@Data
public class GatewayModel extends Model {

    private String proxy;

    private ProviderModel providerModel;

    private LimitModel limitModel;

    public GatewayModel(String proxy, ProviderModel providerModel, LimitModel limitModel) {
        this.proxy = proxy;
        this.providerModel = providerModel;
        this.limitModel = limitModel;
    }

}
