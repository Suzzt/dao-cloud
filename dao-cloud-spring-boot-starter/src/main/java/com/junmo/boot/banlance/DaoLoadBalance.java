package com.junmo.boot.banlance;

import com.junmo.boot.bootstrap.ChannelClient;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/11 22:36
 * @description:
 */
public abstract class DaoLoadBalance {

//    public Set<ChannelClient> available(Set<ChannelClient> channelClients) {
//        return CollectionUtil.removeNull(channelClients);
//    }

    /**
     * route channel client
     *
     * @param availableChannelClients
     * @return
     */
    public abstract ChannelClient route(Set<ChannelClient> availableChannelClients);
}
