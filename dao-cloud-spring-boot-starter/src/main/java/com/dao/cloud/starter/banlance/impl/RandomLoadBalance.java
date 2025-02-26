package com.dao.cloud.starter.banlance.impl;

import cn.hutool.core.util.RandomUtil;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.unit.Client;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0
 */
public class RandomLoadBalance extends DaoLoadBalance {
    @Override
    public Client route(Set<Client> availableClients) {
        Client[] clients = availableClients.toArray(new Client[availableClients.size()]);
        int index = RandomUtil.randomInt(availableClients.size());
        return clients[index];
    }
}
