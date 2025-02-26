package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * Synchronize service node data to the cluster
 */
@Data
public class ServiceShareClusterRequestModel extends AbstractShareClusterRequestModel{
    /**
     * Service node change information
     */
    private RegisterProviderModel registerProviderModel;
}
