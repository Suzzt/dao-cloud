package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/7/15 23:30
 * @description:
 */
@Data
public class ClusterSyncDataModel extends NumberingModel {
    /**
     * type
     * -2: remove the configuration from the configuration center
     * -1: indicates that the service is down from the cluster
     * 1: indicates that the service is added to the cluster
     * 2: save the configuration from the configuration center
     */
    private byte type;
}
