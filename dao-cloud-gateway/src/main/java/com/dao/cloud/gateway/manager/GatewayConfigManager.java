package com.dao.cloud.gateway.manager;

import com.dao.cloud.core.model.GatewayConfigModel;
import com.dao.cloud.core.model.ProxyProviderModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2024/2/14 22:13
 * @description:
 */
public class GatewayConfigManager {

    private static Map<ProxyProviderModel, GatewayConfigModel> data;

    private static Map<ProxyProviderModel, GatewayConfig> config = new ConcurrentHashMap<>();

    public static void reset(Map<ProxyProviderModel, GatewayConfigModel> config) {
        data = config;
    }

    public static GatewayConfigModel getGatewayConfig(ProxyProviderModel proxyProviderModel) {
        return data.get(proxyProviderModel);
    }

    public static GatewayConfig getGatewayConfig2(ProxyProviderModel proxyProviderModel) {
        return config.get(proxyProviderModel);
    }

}
