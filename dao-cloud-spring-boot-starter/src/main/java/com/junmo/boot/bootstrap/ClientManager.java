package com.junmo.boot.bootstrap;

import com.junmo.boot.channel.ChannelClient;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2023/1/23 21:07
 * @description: todo 这个有线程安全问题
 */
public class ClientManager {
    private static Map<String, Set<ChannelClient>> clients = new ConcurrentHashMap<>();

    public static Set<ChannelClient> getClients(String proxy) {
        return clients.get(proxy);
    }

    /**
     * add client
     *
     * @param proxy
     * @param channelClient
     */
    public static void add(String proxy, ChannelClient channelClient) {
        if (CollectionUtils.isEmpty(clients) || CollectionUtils.isEmpty(clients.get(proxy))) {
            Set<ChannelClient> channelClients = new HashSet<>();
            channelClients.add(channelClient);
            clients.put(proxy, channelClients);
        } else {
            Set<ChannelClient> channelClients = clients.get(proxy);
            channelClients.add(channelClient);
        }
    }

    /**
     * add clients
     *
     * @param proxy
     * @param channelClients
     */
    public static void addAll(String proxy, Set<ChannelClient> channelClients) {
        if (CollectionUtils.isEmpty(clients) || CollectionUtils.isEmpty(clients.get(proxy))) {
            clients.put(proxy, channelClients);
        } else {
            Set<ChannelClient> channelClientSet = clients.get(proxy);
            channelClientSet.addAll(channelClients);
        }
    }

    /**
     * remove client
     *
     * @param proxy
     * @param channelClient
     */
    public static void remove(String proxy, ChannelClient channelClient) {
        Set<ChannelClient> channelClientSet = clients.get(proxy);
        channelClientSet.remove(channelClient);
    }

    /**
     * remove clients
     *
     * @param proxy
     * @param channelClients
     */
    public static void removeAll(String proxy, Set<ChannelClient> channelClients) {
        Set<ChannelClient> channelClientSet = clients.get(proxy);
        channelClientSet.removeAll(channelClients);
    }
}
