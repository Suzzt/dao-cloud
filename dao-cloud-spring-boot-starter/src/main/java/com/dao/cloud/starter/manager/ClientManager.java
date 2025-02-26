package com.dao.cloud.starter.manager;

import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.starter.unit.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sucf
 * @since 1.0
 */
public class ClientManager {

    /**
     * 客户端Bootstrap
     */
    private static volatile Bootstrap RPC_BOOTSTRAP;

    /**
     * share connection pool
     */
    private final static Map<ServerNodeModel, Client> SHARED_CONNECT_CLIENTS = new ConcurrentHashMap<>();

    /**
     * clients
     * key: proxy + provider + version
     * value: service node clients
     */
    private final static Map<ProxyProviderModel, Set<ServerNodeModel>> SERVICE_NODES = new ConcurrentHashMap<>();

    public static Bootstrap getRpcBootstrap() {
        if (RPC_BOOTSTRAP == null) {
            synchronized (ClientManager.class) {
                if (RPC_BOOTSTRAP == null) {
                    RPC_BOOTSTRAP = new Bootstrap().channel(NioSocketChannel.class).group(new NioEventLoopGroup());
                }
            }
        }
        return RPC_BOOTSTRAP;
    }

    /**
     * get provider nodes
     *
     * @param proxyProviderModel
     * @return
     */
    public static Set<ServerNodeModel> getProviderNodes(ProxyProviderModel proxyProviderModel) {
        return SERVICE_NODES.get(proxyProviderModel);
    }

    /**
     * get available provider nodes
     *
     * @param proxyProviderModel
     * @return
     */
    public static Set<ServerNodeModel> getAvailableProviderNodes(ProxyProviderModel proxyProviderModel) {
        Set<ServerNodeModel> availableNodes = new HashSet<>();
        Set<ServerNodeModel> providerNodes = getProviderNodes(proxyProviderModel);
        if (CollectionUtils.isEmpty(providerNodes)) {
            return null;
        }
        for (ServerNodeModel serverNodeModel : providerNodes) {
            if (serverNodeModel.isStatus()) {
                availableNodes.add(serverNodeModel);
            }
        }
        return availableNodes;
    }

    /**
     * save provider service node
     *
     * @param proxyProviderModel
     * @param providerNodes
     */
    public static void save(ProxyProviderModel proxyProviderModel, Set<ServerNodeModel> providerNodes) {
        providerNodes = providerNodes == null ? new HashSet<>() : providerNodes;
        SERVICE_NODES.put(proxyProviderModel, providerNodes);
        for (ServerNodeModel providerNode : providerNodes) {
            SHARED_CONNECT_CLIENTS.putIfAbsent(providerNode, new Client(proxyProviderModel, providerNode.getIp(), providerNode.getPort()));
        }
    }

    /**
     * remove provider service node
     *
     * @param providerNode
     */
    public static void remove(ServerNodeModel providerNode) {
        // 移除掉此长连接依赖的所有提供者rpc调用的ProxyProvider
        for (Map.Entry<ProxyProviderModel, Set<ServerNodeModel>> entry : SERVICE_NODES.entrySet()) {
            Set<ServerNodeModel> serverNodeModels = entry.getValue();
            if (!CollectionUtils.isEmpty(serverNodeModels)) {
                serverNodeModels.remove(providerNode);
            }
        }
        // 移除掉连接池中的此长连接
        Client client = SHARED_CONNECT_CLIENTS.get(providerNode);
        client.destroy();
        SHARED_CONNECT_CLIENTS.remove(providerNode);
    }

    /**
     * get shared client
     *
     * @param serverNodeModels
     * @return
     */
    public static Set<Client> getSharedClient(Set<ServerNodeModel> serverNodeModels) {
        Set<Client> set = new HashSet<>();
        for (ServerNodeModel serverNodeModel : serverNodeModels) {
            Client client = SHARED_CONNECT_CLIENTS.get(serverNodeModel);
            if (client != null) {
                set.add(client);
            }
        }
        return set;
    }
}
