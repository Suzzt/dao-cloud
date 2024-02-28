package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/27 23:13
 * @description: Synchronize service node data to the cluster
 */
@Data
public class ServiceShareClusterRequestModel extends AbstractShareClusterRequestModel{
    /**
     * Service node change information
     */
    private RegisterProviderModel registerProviderModel;
}
