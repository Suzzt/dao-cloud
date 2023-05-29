package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/5/30 00:11
 * @description:
 */
@Data
public class ClusterSyncServerModel extends Model {
    /**
     * flag
     * -1: indicates that the service is down from the cluster
     * 1: indicates that the service is added to the cluster
     */
    private byte flag;
    private ProxyProviderModel proxyProviderModel;
    private ServerNodeModel serverNodeModel;
}
