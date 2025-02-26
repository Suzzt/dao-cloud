package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * Synchronization of server configuration information between clusters
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
