package com.junmo.boot.banlance;

import com.junmo.boot.bootstrap.unit.Client;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/11 22:36
 * @description:
 */
public abstract class DaoLoadBalance {

    /**
     * route channel client
     *
     * @param availableClients
     * @return
     */
    public abstract Client route(Set<Client> availableClients);
}
