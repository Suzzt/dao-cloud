package com.junmo.boot.bootstrap;

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
    /**
     * clients
     * key: proxy+'#'+version
     * value: channel clients
     */
    private static Map<String, Set<ChannelClient>> clients = new ConcurrentHashMap<>();

    public static Set<ChannelClient> getClients(String proxy, int version) {
        return clients.get(makeKey(proxy, version));
    }

    private static String makeKey(String proxy, int version) {
        return proxy + "#" + version;
    }

    /**
     * add client
     *
     * @param proxy
     * @param channelClient
     */
    public static void add(String proxy, int version, ChannelClient channelClient) {
        String key = makeKey(proxy, version);
        if (CollectionUtils.isEmpty(clients) || CollectionUtils.isEmpty(clients.get(key))) {
            Set<ChannelClient> channelClients = new HashSet<>();
            channelClients.add(channelClient);
            clients.put(key, channelClients);
        } else {
            Set<ChannelClient> channelClients = clients.get(key);
            channelClients.add(channelClient);
        }
    }

    /**
     * add clients
     *
     * @param proxy
     * @param version
     * @param channelClients
     */
    public static void addAll(String proxy, int version, Set<ChannelClient> channelClients) {
        String key = makeKey(proxy, version);
        if (CollectionUtils.isEmpty(clients) || CollectionUtils.isEmpty(clients.get(key))) {
            clients.put(key, channelClients);
        } else {
            Set<ChannelClient> channelClientSet = clients.get(key);
            channelClientSet.addAll(channelClients);
        }
    }

    /**
     * remove client
     *
     * @param proxy
     * @param version
     * @param channelClient
     */
    public static void remove(String proxy, int version, ChannelClient channelClient) {
        String key = makeKey(proxy, version);
        Set<ChannelClient> channelClientSet = clients.get(key);
        channelClientSet.remove(channelClient);
    }

    /**
     * remove clients
     *
     * @param proxy
     * @param version
     * @param channelClients
     */
    public static void removeAll(String proxy, int version, Set<ChannelClient> channelClients) {
        String key = makeKey(proxy, version);
        Set<ChannelClient> channelClientSet = clients.get(key);
        channelClientSet.removeAll(channelClients);
    }
}
