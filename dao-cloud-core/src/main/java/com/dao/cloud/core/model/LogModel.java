package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/8/22 23:19
 * @description: log data model
 */
@Data
public class LogModel extends Model {
    private String traceId;
    /**
     * log stage
     * 1-1-2
     */
    private String stage;
    private ProxyProviderModel proxyProviderModel;
    /**
     * Processing node info
     * ip+port
     */
    private String node;
    private String log;
}
