package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/5/30 00:11
 * @description:
 */
@Data
public class ClusterSyncDataRequestModel extends ClusterSyncDataModel {
    private RegisterProviderModel registerProviderModel;
    private ProxyConfigModel proxyConfigModel;
    private String configJson;
}
