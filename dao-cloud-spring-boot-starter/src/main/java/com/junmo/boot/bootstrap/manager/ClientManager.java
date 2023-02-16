package com.junmo.boot.bootstrap.manager;

import com.junmo.boot.bootstrap.unit.Client;
import com.junmo.core.model.ProxyProviderModel;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2023/1/23 21:07
 * @description:
 */
public class ClientManager {
    /**
     * clients
     * key: proxy+provider+version
     * value: channel clients
     */
    private static Map<ProxyProviderModel, Set<Client>> clients = new ConcurrentHashMap<>();

    public static Set<Client> getClients(ProxyProviderModel proxyProviderModel) {
        return clients.get(proxyProviderModel);
    }


    /**
     * add clients
     *
     * @param proxyProviderModel
     * @param clients
     */
    public static void addAll(ProxyProviderModel proxyProviderModel, Set<Client> clients) {
        if (CollectionUtils.isEmpty(ClientManager.clients) || CollectionUtils.isEmpty(ClientManager.clients.get(proxyProviderModel))) {
            ClientManager.clients.put(proxyProviderModel, clients);
        } else {
            Set<Client> clientSet = ClientManager.clients.get(proxyProviderModel);
            clientSet.addAll(clients);
        }
    }

    /**
     * remove client
     *
     * @param proxyProviderModel
     * @param client
     */
    public static void remove(ProxyProviderModel proxyProviderModel, Client client) {
        Set<Client> clientSet = clients.get(proxyProviderModel);
        clientSet.remove(client);
    }

}
