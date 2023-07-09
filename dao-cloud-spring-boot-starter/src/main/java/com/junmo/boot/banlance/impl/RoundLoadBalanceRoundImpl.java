package com.junmo.boot.banlance.impl;

import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.unit.Client;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: sucf
 * @date: 2023/1/11 22:39
 * @description:
 */
public class RoundLoadBalanceRoundImpl extends DaoLoadBalance {

    private AtomicInteger count = new AtomicInteger();

    @Override
    public Client route(Set<Client> channelClients) {
        Client[] clients = channelClients.toArray(new Client[channelClients.size()]);
        count.incrementAndGet();
        return clients[count.get() % channelClients.size()];
    }
}