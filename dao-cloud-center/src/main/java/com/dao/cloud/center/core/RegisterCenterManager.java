package com.dao.cloud.center.core;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.expand.Persistence;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
     * value: server nodes --->ip + port + status
     */
    private final Map<String, Map<ProviderModel, Set<ServerNodeModel>>> SERVER = new HashMap<>();


    private final Persistence persistence;

    public RegisterCenterManager(Persistence persistence) {
        this.persistence = persistence;
    }

    /**
     * 获取整个注册中心服务信息
     *
     * @return
     */
    public Map<String, Map<ProviderModel, Set<ServerNodeModel>>> getServer() {
        return SERVER;
    }

    /**
     * 获取整个注册中心服务信息(todo 看看这里SERVER对象数据结构后面能不能替换掉)
     * 去除网关本身节点信息(gateway)
     *
     * @return
     */
    public Map<ProxyProviderModel, Set<ServerNodeModel>> gatewayServers() {
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
    public int methods() {
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
    public int nodes() {
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
    public int gatewayCountNodes() {
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
    public synchronized void registry(RegisterProviderModel registerProviderModel) {
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
    public synchronized void add(String proxy, ProviderModel providerModel, ServerNodeModel serverNodeModel) {
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
    public synchronized void unregistered(RegisterProviderModel registerProviderModel) {
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
    public Set<ServerNodeModel> getServers(String proxy, ProviderModel providerModel) {
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

    /**
     * 服务管理(上下线)
     *
     * @param proxy
     * @param provider
     * @param version
     * @param serverNodeModel
     */
    public synchronized void manage(String proxy, String provider, Integer version, ServerNodeModel serverNodeModel) {
        ProviderModel providerModel = new ProviderModel(provider, version);
        Set<ServerNodeModel> serverNodeModels = SERVER.get(proxy).get(providerModel);
        for (ServerNodeModel node : serverNodeModels) {
            if (node.equals(serverNodeModel)) {
                node.setStatus(serverNodeModel.isStatus());
                ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, providerModel);
                persistence.storage(proxyProviderModel, serverNodeModel);
                break;
            }
        }
    }
}
