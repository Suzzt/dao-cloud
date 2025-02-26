package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/3/31 23:51
 * @description: Synchronization of server configuration information between clusters
 */
@Data
public class ServerShareClusterRequestModel extends AbstractShareClusterRequestModel {
    /**
     * service information key
     */
    private ProxyProviderModel proxyProviderModel;

    /**
     * server node information
     */
    private ServerNodeModel serverNodeModel;
}
