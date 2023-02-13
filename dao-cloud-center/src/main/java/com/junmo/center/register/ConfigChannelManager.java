package com.junmo.center.register;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.junmo.core.model.ProxyProviderModel;
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
     * config manager
     * key: proxy + '#' + version
     * value: channels
     */
    private static Map<String, Set<Channel>> CONFIG_CHANNEL_MAP = Maps.newConcurrentMap();

    public static Set<Channel> getConfigChannel(String proxyKey) {
        return CONFIG_CHANNEL_MAP.get(proxyKey);
    }

    public static Set<Channel> getConfigChannel(ProxyProviderModel proxyProviderModel) {
        return null;
    }

    public static synchronized void add(String proxyKey, Channel channel) {
        Set<Channel> channels = CONFIG_CHANNEL_MAP.get(proxyKey);
        if (CollectionUtils.isEmpty(channels)) {
            channels = Sets.newHashSet();
        }
        channels.add(channel);
        CONFIG_CHANNEL_MAP.put(proxyKey, channels);
    }

    public static synchronized void remove(String proxyKey, Channel channel) {
        Set<Channel> channels = CONFIG_CHANNEL_MAP.get(proxyKey);
        channels.remove(channel);
    }
}