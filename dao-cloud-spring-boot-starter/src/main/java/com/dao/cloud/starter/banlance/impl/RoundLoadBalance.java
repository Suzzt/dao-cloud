package com.dao.cloud.starter.banlance.impl;

import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.unit.Client;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sucf
 * @since 1.0
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
