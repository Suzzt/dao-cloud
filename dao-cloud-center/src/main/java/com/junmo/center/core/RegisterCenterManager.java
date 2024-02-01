package com.junmo.center.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.DaoCloudConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private final static Map<String, Map<ProviderModel, Set<ServerNodeModel>>> SERVER = new HashMap<>();

    /**
     * 获取整个注册中心服务信息
     *
     * @return
     */
    public static Map<String, Map<ProviderModel, Set<ServerNodeModel>>> getServer() {
        return SERVER;
    }

    /**
     * 获取整个注册中心服务信息(todo 看看这里SERVER对象数据结构后面能不能替换掉)
     * 去除网关本身节点信息(gateway)
     *
     * @return
     */
    public static Map<ProxyProviderModel, Set<ServerNodeModel>> gatewayServers() {
        Map<ProxyProviderModel, Set<ServerNodeModel>> conversionObject = new HashMap<>();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : SERVER.entrySet()) {
            String proxy = entry.getKey();
            if (DaoCloudConstant.GATEWAY_PROXY.equals(proxy)) {
                continue;
            }
            Map<ProviderModel, Set<ServerNodeModel>> providerModels = entry.getValue();
            for (Map.Entry<ProviderModel, Set<ServerNodeModel>> providerModelSetEntry : providerModels.entrySet()) {
                ProviderModel providerModel = providerModelSetEntry.getKey();
                ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, providerModel);
                conversionObject.put(proxyProviderModel, providerModelSetEntry.getValue());
            }
        }
        return conversionObject;
    }

    /**
     * 服务可调用方法数
     * distinct (key + provider + version)
     *
     * @return
     */
    public static int methods() {
        int i = 0;
        Set<Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>>> entries = SERVER.entrySet();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : entries) {
            Map<ProviderModel, Set<ServerNodeModel>> map = entry.getValue();
            for (Map.Entry<ProviderModel, Set<ServerNodeModel>> providerModelSetEntry : map.entrySet()) {
                Set<ServerNodeModel> nodes = providerModelSetEntry.getValue();
                if (nodes != null && nodes.size() > 0) {
                    i++;
                }
            }
        }
        return i;
    }

    /**
     * 服务节点数
     *
     * @return count distinct (ip + port)
     */
    public static int nodes() {
        Set<ServerNodeModel> temp = new HashSet<>();
        Set<Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>>> entries = SERVER.entrySet();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : entries) {
            Map<ProviderModel, Set<ServerNodeModel>> map = entry.getValue();
            for (Map.Entry<ProviderModel, Set<ServerNodeModel>> providerModelSetEntry : map.entrySet()) {
                Set<ServerNodeModel> nodes = providerModelSetEntry.getValue();
                temp.addAll(nodes);
            }
        }
        return temp.size();
    }

    /**
     * 网关节点数
     *
     * @return 网关在整个系统的节点数
     */
    public static int gatewayCountNodes() {
        int i = 0;
        Set<Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>>> entries = SERVER.entrySet();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : entries) {
            if (DaoCloudConstant.GATEWAY_PROXY.equals(entry.getKey())) {
                Map<ProviderModel, Set<ServerNodeModel>> map = entry.getValue();
                for (Map.Entry<ProviderModel, Set<ServerNodeModel>> providerModelSetEntry : map.entrySet()) {
                    ProviderModel providerModel = providerModelSetEntry.getKey();
                    if (DaoCloudConstant.GATEWAY.equals(providerModel.getProvider())) {
                        i += providerModelSetEntry.getValue().size();
                    }
                }
            }
        }
        return i;
    }

    /**
     * 注册节点
     *
     * @param registerProviderModel
     */
    public static synchronized void register(RegisterProviderModel registerProviderModel) {
        String proxy = registerProviderModel.getProxy();
        Set<ProviderModel> registerProviders = registerProviderModel.getProviderModels();
        ServerNodeModel serverNodeModel = registerProviderModel.getServerNodeModel();
        for (ProviderModel providerModel : registerProviders) {
            add(proxy, providerModel, serverNodeModel);
        }
    }

    /**
     * 增加节点
     *
     * @param proxy
     * @param providerModel
     * @param serverNodeModel
     */
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

    /**
     * 移除节点
     *
     * @param registerProviderModel
     */
    public static synchronized void down(RegisterProviderModel registerProviderModel) {
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

    /**
     * 获取节点服务信息
     *
     * @param proxy
     * @param providerModel
     * @return
     */
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
