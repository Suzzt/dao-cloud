package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/5/30 00:11
 * @description: cluster 各个节点的数据同步
 */
@Data
public class ClusterSyncDataRequestModel extends NumberingModel {
    /**
     * type
     * -2: remove the configuration from the configuration center
     * -1: indicates that the service is down from the cluster
     * 1: indicates that the service is added to the cluster
     * 2: save the configuration from the configuration center
     */
    private byte type;

    /**
     * 注册服务信息
     */
    private RegisterProviderModel registerProviderModel;

    /**
     * 配置类别信息
     */
    private ProxyConfigModel proxyConfigModel;

    /**
     * 配置数据内容
     */
    private String configJson;
}
