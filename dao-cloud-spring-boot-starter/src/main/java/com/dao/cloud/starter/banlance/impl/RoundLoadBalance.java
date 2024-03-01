package com.dao.cloud.starter.banlance.impl;

import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.bootstrap.unit.Client;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: sucf
 * @date: 2023/1/11 22:39
 * @description:
 */
public class RoundLoadBalance extends DaoLoadBalance {

    private AtomicInteger count = new AtomicInteger();

    @Override
    public Client route(Set<Client> channelClients) {
        Client[] clients = channelClients.toArray(new Client[channelClients.size()]);
        count.incrementAndGet();
        return clients[count.get() % channelClients.size()];
    }
}
