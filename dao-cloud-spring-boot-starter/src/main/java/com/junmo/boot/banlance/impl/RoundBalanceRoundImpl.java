package com.junmo.boot.banlance.impl;

import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.channel.ChannelClient;
import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: sucf
 * @date: 2023/1/11 22:39
 * @description:
 */
public class RoundBalanceRoundImpl implements DaoLoadBalance {

    private AtomicInteger count = new AtomicInteger();

    @Override
    public Channel route(Set<ChannelClient> channelClients) {
        ChannelClient[] clients = channelClients.toArray(new ChannelClient[channelClients.size()]);
        count.incrementAndGet();
        return clients[count.get() % channelClients.size()].getChannel();
    }
}
