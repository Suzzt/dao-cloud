package com.dao.cloud.starter.banlance.impl;

import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.unit.Client;

import java.util.ArrayList;
import java.util.List;
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
        // Fallback to consistent hashing with client info when no hash key provided
        List<Client> clientList = new ArrayList<>(availableClients);
        Client firstClient = clientList.get(0);
        String defaultHashKey = firstClient.getIp() + ":" + firstClient.getPort();
        return route(availableClients, defaultHashKey);
    }

    @Override
    public Client route(Set<Client> availableClients, Object hashKey) {
        if (availableClients == null || availableClients.isEmpty()) {
            return null;
        }

        if (availableClients.size() == 1) {
            return availableClients.iterator().next();
        }

        List<Client> clientList = new ArrayList<>(availableClients);
        
        // Use hash key for consistent hashing
        String keyStr = hashKey != null ? hashKey.toString() : "";
        int hash = keyStr.hashCode();
        
        // Simple consistent hashing implementation
        int index = Math.abs(hash) % clientList.size();
        return clientList.get(index);
    }
}
