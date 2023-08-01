package com.junmo.center.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.junmo.core.model.ProxyConfigModel;
import io.netty.channel.Channel;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/2/11 23:59
 * @description: config channel manager
 */
public class ConfigChannelManager {
    /**
     * 配置订阅的channel
     */
    private final static Map<ProxyConfigModel, Set<Channel>> CONFIG_CHANNEL_MAP = Maps.newConcurrentMap();

    /**
     * 每个订阅服务数的总和
     *
     * @return
     */
    public static int size() {
        int i = 0;
        for (Map.Entry<ProxyConfigModel, Set<Channel>> entry : CONFIG_CHANNEL_MAP.entrySet()) {
            Set<Channel> set = entry.getValue();
            if (!CollectionUtils.isEmpty(set)) {
                i = i + set.size();
            }
        }
        return i;
    }

    public static Set<Channel> getSubscribeChannel(ProxyConfigModel proxyConfigModel) {
        return CONFIG_CHANNEL_MAP.get(proxyConfigModel);
    }

    public static synchronized void add(ProxyConfigModel proxyConfigModel, Channel channel) {
        Set<Channel> channels = CONFIG_CHANNEL_MAP.get(proxyConfigModel);
        if (CollectionUtils.isEmpty(channels)) {
            channels = Sets.newHashSet();
        }
        channels.add(channel);
        CONFIG_CHANNEL_MAP.put(proxyConfigModel, channels);
    }

    public static synchronized void remove(ProxyConfigModel proxyConfigModel, Channel channel) {
        Set<Channel> channels = CONFIG_CHANNEL_MAP.get(proxyConfigModel);
        channels.remove(channel);
    }
}