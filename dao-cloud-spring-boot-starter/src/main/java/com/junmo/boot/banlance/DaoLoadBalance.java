package com.junmo.boot.banlance;

import com.junmo.boot.channel.ChannelClient;
import io.netty.channel.Channel;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/11 22:36
 * @description:
 */
public interface DaoLoadBalance {
    /**
     * route channel
     *
     * @param channelClients
     * @return
     */
    Channel route(Set<ChannelClient> channelClients);
}
