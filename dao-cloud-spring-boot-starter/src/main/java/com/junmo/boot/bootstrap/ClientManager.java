package com.junmo.boot.bootstrap;

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
     * key: proxy+'#'+version
     * value: channel clients
     */
    private static Map<ProxyProviderModel, Set<ChannelClient>> clients = new ConcurrentHashMap<>();

    public static Set<ChannelClient> getClients(ProxyProviderModel proxyProviderModel) {
        return clients.get(proxyProviderModel);
    }


    /**
     * add clients
     *
     * @param proxyProviderModel
     * @param channelClients
     */
    public static void addAll(ProxyProviderModel proxyProviderModel, Set<ChannelClient> channelClients) {
        if (CollectionUtils.isEmpty(clients) || CollectionUtils.isEmpty(clients.get(proxyProviderModel))) {
            clients.put(proxyProviderModel, channelClients);
        } else {
            Set<ChannelClient> channelClientSet = clients.get(proxyProviderModel);
            channelClientSet.addAll(channelClients);
        }
    }

    /**
     * remove client
     *
     * @param proxyProviderModel
     * @param channelClient
     */
    public static void remove(ProxyProviderModel proxyProviderModel, ChannelClient channelClient) {
        Set<ChannelClient> channelClientSet = clients.get(proxyProviderModel);
        channelClientSet.remove(channelClient);
    }

}
