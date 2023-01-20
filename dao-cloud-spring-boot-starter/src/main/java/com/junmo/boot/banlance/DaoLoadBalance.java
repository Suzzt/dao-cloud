package com.junmo.boot.banlance;

import com.junmo.boot.channel.ChannelClient;
import com.junmo.core.enums.Constant;
import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/11 22:36
 * @description:
 */
public abstract class DaoLoadBalance {

    public Set<ChannelClient> available(Set<ChannelClient> channelClients) {
        Set<ChannelClient> result = new HashSet<>();
        for (ChannelClient channelClient : channelClients) {
            if (channelClient.getState() == Constant.CHANNEL_ALIVE_CONNECT_STATE) {
                result.add(channelClient);
            }
        }
        return result;
    }

    /**
     * route channel
     *
     * @param availableChannelClients
     * @return
     */
    public abstract Channel route(Set<ChannelClient> availableChannelClients);
}
