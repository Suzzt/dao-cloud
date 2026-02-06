package com.dao.cloud.starter.banlance;

import com.dao.cloud.starter.unit.Client;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/1/11 22:36
 */
public abstract class DaoLoadBalance {

    /**
     * route channel client
     *
     * @param availableClients
     * @return
     */
    public abstract Client route(Set<Client> availableClients);

    /**
     * route channel client with hash key
     *
     * @param availableClients
     * @param hashKey hash key for consistent hashing, can be null
     * @return
     */
    public Client route(Set<Client> availableClients, Object hashKey) {
        return route(availableClients);
    }
}
