package com.junmo.center.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.model.ServerNodeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/13 22:52
 * @description: server register manager
 */
@Slf4j
public class RegisterCenterManager {

    /**
     * register servers
     * key: proxy
     * value: provider server
     * key: provider + version
     * value: server nodes --->ip + port
     */
    private final static Map<String, Map<ProviderModel, Set<ServerNodeModel>>> SERVER = new ConcurrentHashMap<>();

    public static Map<String, Map<ProviderModel, Set<ServerNodeModel>>> getServer() {
        return SERVER;
    }

    public static synchronized void register(RegisterProviderModel registerProviderModel) {
        String proxy = registerProviderModel.getProxy();
        Set<ProviderModel> registerProviders = registerProviderModel.getProviderModels();
        ServerNodeModel serverNodeModel = registerProviderModel.getServerNodeModel();
        for (ProviderModel providerModel : registerProviders) {
            add(proxy, providerModel, serverNodeModel);
        }
    }

    public static synchronized void add(String proxy, ProviderModel providerModel, ServerNodeModel serverNodeModel) {
        if (SERVER.containsKey(proxy)) {
            Map<ProviderModel, Set<ServerNodeModel>> providerMap = SERVER.get(proxy);
            Set<ServerNodeModel> serverNodeModels = providerMap.get(providerModel);
            if (CollectionUtils.isEmpty(serverNodeModels)) {
                serverNodeModels = Sets.newHashSet();
            }
            serverNodeModels.add(serverNodeModel);
            providerMap.put(providerModel, serverNodeModels);
        } else {
            Map<ProviderModel, Set<ServerNodeModel>> providerMap = Maps.newHashMap();
            Set<ServerNodeModel> serverNodeModels = Sets.newHashSet();
            serverNodeModels.add(serverNodeModel);
            providerMap.put(providerModel, serverNodeModels);
            SERVER.put(proxy, providerMap);
        }
        log.info(">>>>>>>>>>>> proxy({}, {}, {}) register success <<<<<<<<<<<<", proxy, providerModel, serverNodeModel);
    }

    public static void down(RegisterProviderModel registerProviderModel) {
        String proxy = registerProviderModel.getProxy();
        Set<ProviderModel> providerModels = registerProviderModel.getProviderModels();
        ServerNodeModel serverNodeModel = registerProviderModel.getServerNodeModel();
        Map<ProviderModel, Set<ServerNodeModel>> registerProviders = SERVER.get(proxy);
        for (ProviderModel providerModel : providerModels) {
            Set<ServerNodeModel> serverNodeModels = registerProviders.get(providerModel);
            serverNodeModels.remove(serverNodeModel);
        }
        log.error(">>>>>>>>>>> down server proxy ({}, {}, {}) <<<<<<<<<<<", proxy, providerModels, serverNodeModel);
    }

    public static Set<ServerNodeModel> getServers(String proxy, ProviderModel providerModel) {
        if (!StringUtils.hasLength(proxy)) {
            throw new DaoException("proxy = " + proxy + " is null");
        }
        Map<ProviderModel, Set<ServerNodeModel>> registerProviders = SERVER.get(proxy);
        if (CollectionUtils.isEmpty(registerProviders)) {
            return null;
        }
        Set<ServerNodeModel> serverNodeModels = registerProviders.get(providerModel);
        return serverNodeModels;
    }
}
