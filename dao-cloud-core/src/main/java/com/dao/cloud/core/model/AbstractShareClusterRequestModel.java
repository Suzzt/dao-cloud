package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/27 18:03
 * @description: Cluster synchronization data model
 */
@Data
public class AbstractShareClusterRequestModel extends Model {
    /**
     * synchronization type
     * -3: remove gateway configuration information
     * -2: remove the configuration from the configuration center
     * -1: indicates that the service is down from the cluster
     * 1: indicates that the service is added to the cluster
     * 2: save the configuration from the configuration center
     * 3: save gateway configuration information
     * detail see com.dao.cloud.center.core.handler.SyncClusterInformationRequestHandler
     */
    private byte type;

    /**
     * request sequence number
     */
    private long sequenceId;
}