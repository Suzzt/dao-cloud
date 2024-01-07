package com.junmo.gateway.manager;

import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.ServerNodeModel;

import java.util.HashMap;
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
    private final static Map<String, Map<ProviderModel, Set<ServerNodeModel>>> SERVICE_RESOURCE = new HashMap<>();
}
