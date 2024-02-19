package com.junmo.gateway.global;

import com.junmo.core.model.GatewayConfigModel;
import com.junmo.core.model.ProxyProviderModel;

import java.util.Map;

/**
 * @author: sucf
 * @date: 2024/2/14 22:13
 * @description:
 */
public class GatewayServiceConfig {

    private static Map<ProxyProviderModel, GatewayConfigModel> data;

    public static void reset(Map<ProxyProviderModel, GatewayConfigModel> config) {
        data = config;
    }

    public static GatewayConfigModel getGatewayConfig(ProxyProviderModel proxyProviderModel) {
        return data.get(proxyProviderModel);
    }
}