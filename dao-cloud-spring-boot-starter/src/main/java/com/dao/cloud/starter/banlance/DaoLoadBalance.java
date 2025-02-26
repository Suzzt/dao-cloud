package com.dao.cloud.starter.banlance;

import com.dao.cloud.starter.unit.Client;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0
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
