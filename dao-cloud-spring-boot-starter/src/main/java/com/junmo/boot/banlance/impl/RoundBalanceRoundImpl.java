package com.junmo.boot.banlance.impl;

import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.ChannelClient;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: sucf
 * @date: 2023/1/11 22:39
 * @description:
 */
public class RoundBalanceRoundImpl extends DaoLoadBalance {

    private AtomicInteger count = new AtomicInteger();

    @Override
    public ChannelClient route(Set<ChannelClient> channelClients) {
        ChannelClient[] clients = channelClients.toArray(new ChannelClient[channelClients.size()]);
        count.incrementAndGet();
        return clients[count.get() % channelClients.size()];
    }
}
