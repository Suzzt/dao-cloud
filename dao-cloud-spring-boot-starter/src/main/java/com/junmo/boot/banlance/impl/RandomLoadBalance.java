package com.junmo.boot.banlance.impl;

import cn.hutool.core.util.RandomUtil;
import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.unit.Client;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/11 22:39
 * @description:
 */
public class RandomLoadBalance extends DaoLoadBalance {
    @Override
    public Client route(Set<Client> availableClients) {
        Client[] clients = availableClients.toArray(new Client[availableClients.size()]);
        int index = RandomUtil.randomInt(availableClients.size());
        return clients[index];
    }
}
