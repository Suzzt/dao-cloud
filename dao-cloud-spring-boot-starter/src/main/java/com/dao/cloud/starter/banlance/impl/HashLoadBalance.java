package com.dao.cloud.starter.banlance.impl;

import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.unit.Client;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/7/6 23:59
 * Hash Load Balance
 */
public class HashLoadBalance extends DaoLoadBalance {
    @Override
    public Client route(Set<Client> availableClients) {
        // 有缘人自己实现吧
        return null;
    }
}
