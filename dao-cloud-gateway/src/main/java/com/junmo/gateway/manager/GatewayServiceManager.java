package com.junmo.gateway.manager;

import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.ServerNodeModel;

import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2024/1/7 00:34
 * @description: Gateway Service Manager
 * Store service instance information
 */
public class GatewayServiceManager {
    /**
     * register servers
     * key: proxy
     * value: provider server
     * key: provider + version
     * value: server nodes --->ip + port
     */
    private static Map<ProxyProviderModel, Set<ServerNodeModel>> SERVICE_RESOURCE;

    /**
     * 重归置所有服务节点, 这个是一个兜底的解决方案
     */
    public static void reset(Map<ProxyProviderModel, Set<ServerNodeModel>> services) {
        SERVICE_RESOURCE = services;
    }
}
