package com.dao.cloud.center.core;

import com.dao.cloud.center.core.model.ServerProxyProviderNode;
import com.dao.cloud.center.core.model.ServiceNode;
import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.google.common.collect.Maps;
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
     * value: server nodes --->ip + port + service node model
     */
    private final Map<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> REGISTRY_SERVER = new HashMap<>();

    /**
     * server config
     * key: proxy + provider + version + ip + port
     * value: status
     */
    private Map<ServerProxyProviderNode, Boolean> SERVER_CONFIG;

    private final Persistence persistence;

    public void init() {
        SERVER_CONFIG = persistence.loadServer();
    }

    public RegisterCenterManager(Persistence persistence) {
        this.persistence = persistence;
    }

    /**
     * 获取整个注册中心服务信息
     *
     * @return
     */
    public synchronized Map<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> getServer() {
        return REGISTRY_SERVER;
    }

    /**
     * 获取整个注册中心服务信息
     * 去除网关本身节点信息(gateway)
     *
     * @return
     */
    public synchronized Map<ProxyProviderModel, Set<ServerNodeModel>> gatewayServers() {
        Map<ProxyProviderModel, Set<ServerNodeModel>> conversionObject = new HashMap<>();
        for (Map.Entry<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> entry : REGISTRY_SERVER.entrySet()) {
            String proxy = entry.getKey();
            if (DaoCloudConstant.GATEWAY_PROXY.equals(proxy)) {
                continue;
            }
            Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModels = entry.getValue();
            for (Map.Entry<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModelSetEntry : providerModels.entrySet()) {
                ProviderModel providerModel = providerModelSetEntry.getKey();
                ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, providerModel);
                Map<ServiceNode, ServerNodeModel> serverNodeModels = providerModelSetEntry.getValue();
                Set<ServerNodeModel> set = new HashSet<>();
                for (Map.Entry<ServiceNode, ServerNodeModel> serverNodeModelEntry : serverNodeModels.entrySet()) {
                    ServerNodeModel serverNodeModel = serverNodeModelEntry.getValue();
                    if (serverNodeModel.isStatus()) {
                        set.add(serverNodeModel);
                    }
                }
                conversionObject.put(proxyProviderModel, set);
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
        Set<Map.Entry<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>>> entries = REGISTRY_SERVER.entrySet();
        for (Map.Entry<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> entry : entries) {
            Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> map = entry.getValue();
            for (Map.Entry<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModelSetEntry : map.entrySet()) {
                Map<ServiceNode, ServerNodeModel> nodes = providerModelSetEntry.getValue();
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
        Set<ServiceNode> temp = new HashSet<>();
        Set<Map.Entry<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>>> entries = REGISTRY_SERVER.entrySet();
        for (Map.Entry<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> entry : entries) {
            Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> map = entry.getValue();
            for (Map.Entry<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModelSetEntry : map.entrySet()) {
                Map<ServiceNode, ServerNodeModel> nodes = providerModelSetEntry.getValue();
                for (Map.Entry<ServiceNode, ServerNodeModel> serverNodeModelEntry : nodes.entrySet()) {
                    temp.add(serverNodeModelEntry.getKey());
                }
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
        Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModelMapMap = REGISTRY_SERVER.get(DaoCloudConstant.GATEWAY_PROXY);
        if (providerModelMapMap == null) {
            return 0;
        }
        int i = 0;
        for (Map.Entry<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModelSetEntry : providerModelMapMap.entrySet()) {
            ProviderModel providerModel = providerModelSetEntry.getKey();
            if (DaoCloudConstant.GATEWAY.equals(providerModel.getProvider())) {
                i += providerModelSetEntry.getValue().size();
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
            maintain(proxy, providerModel, serverNodeModel);
        }
    }

    /**
     * 维护服务节点
     *
     * @param proxy
     * @param providerModel
     * @param serverNodeModel
     */
    public synchronized void maintain(String proxy, ProviderModel providerModel, ServerNodeModel serverNodeModel) {
        ServerNodeModel assignment = assignment(new ProxyProviderModel(proxy, providerModel), serverNodeModel);
        if (REGISTRY_SERVER.containsKey(proxy)) {
            Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerMap = REGISTRY_SERVER.get(proxy);
            Map<ServiceNode, ServerNodeModel> serverNodeModels = providerMap.get(providerModel);
            if (CollectionUtils.isEmpty(serverNodeModels)) {
                serverNodeModels = Maps.newHashMap();
            }
            serverNodeModels.put(new ServiceNode(serverNodeModel.getIp(), serverNodeModel.getPort()), assignment);
            providerMap.put(providerModel, serverNodeModels);
        } else {
            Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerMap = Maps.newHashMap();
            Map<ServiceNode, ServerNodeModel> serverNodeModels = Maps.newHashMap();
            serverNodeModels.put(new ServiceNode(serverNodeModel.getIp(), serverNodeModel.getPort()), assignment);
            providerMap.put(providerModel, serverNodeModels);
            REGISTRY_SERVER.put(proxy, providerMap);
        }
        log.info(">>>>>>>>>>>> Register service node information({}, {}, {}) success <<<<<<<<<<<<", proxy, providerModel, assignment);
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
        Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> registerProviders = REGISTRY_SERVER.get(proxy);
        for (ProviderModel providerModel : providerModels) {
            Map<ServiceNode, ServerNodeModel> serverNodeModels = registerProviders.get(providerModel);
            serverNodeModels.remove(new ServiceNode(serverNodeModel.getIp(), serverNodeModel.getPort()));
        }
        log.error(">>>>>>>>>>> Down service node information ({}, {}, {}) <<<<<<<<<<<", proxy, providerModels, serverNodeModel);
    }

    /**
     * 获取节点服务信息
     *
     * @param proxy
     * @param providerModel
     * @return
     */
    public synchronized Set<ServerNodeModel> getServers(String proxy, ProviderModel providerModel) {
        Set<ServerNodeModel> set = new HashSet<>();
        if (!StringUtils.hasLength(proxy)) {
            throw new DaoException("proxy = " + proxy + " is null");
        }
        Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> registerProviders = REGISTRY_SERVER.get(proxy);
        if (CollectionUtils.isEmpty(registerProviders)) {
            return null;
        }
        Map<ServiceNode, ServerNodeModel> serverNodeModels = registerProviders.get(providerModel);
        for (Map.Entry<ServiceNode, ServerNodeModel> serverNodeModelEntry : serverNodeModels.entrySet()) {
            set.add(serverNodeModelEntry.getValue());
        }
        return set;
    }


    /**
     * 获取所有服务配置
     *
     * @return
     */
    public synchronized Map<ProxyProviderModel, ServerNodeModel> getConfig() {
        Map<ProxyProviderModel, ServerNodeModel> map = new HashMap<>(16);
        for (Map.Entry<ServerProxyProviderNode, Boolean> entry : SERVER_CONFIG.entrySet()) {
            ServerProxyProviderNode node = entry.getKey();
            ProxyProviderModel proxyProviderModel = node.getProxyProviderModel();
            ServerNodeModel serverNodeModel = new ServerNodeModel(node.getIp(), node.getPort());
            serverNodeModel.setStatus(entry.getValue());
            map.put(proxyProviderModel, serverNodeModel);
        }
        return map;
    }

    /**
     * 服务管理(上下线)
     *
     * @param proxyProviderModel
     * @param serverNodeModel
     */
    public synchronized void manage(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        ProviderModel providerModel = proxyProviderModel.getProviderModel();
        String proxy = proxyProviderModel.getProxy();
        Map<ServiceNode, ServerNodeModel> serverNodeModels = REGISTRY_SERVER.get(proxy).get(providerModel);
        ServerNodeModel serviceNode = serverNodeModels.get(new ServiceNode(serverNodeModel.getIp(), serverNodeModel.getPort()));
        serviceNode.setStatus(serverNodeModel.isStatus());
        SERVER_CONFIG.put(new ServerProxyProviderNode(proxyProviderModel, serverNodeModel.getIp(), serverNodeModel.getPort()), serverNodeModel.isStatus());
        persistence.storage(proxyProviderModel, serverNodeModel);
    }

    /**
     * Assignment status
     *
     * @param proxyProviderModel
     * @param serverNodeModel
     * @return New ServerNodeModel Object
     */
    private ServerNodeModel assignment(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        ServerNodeModel node = new ServerNodeModel(serverNodeModel.getIp(), serverNodeModel.getPort(), serverNodeModel.getPerformance());
        Boolean status = SERVER_CONFIG.get(new ServerProxyProviderNode(proxyProviderModel, serverNodeModel.getIp(), serverNodeModel.getPort()));
        if (status == null) {
            // 从来就没设置过状态, 它应该是null, 那么就是true
            status = true;
        }
        node.setStatus(status);
        return node;
    }
}
