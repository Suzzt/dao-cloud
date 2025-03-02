package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/8/22 23:19
 * log data model
 */
@Data
public class LogModel extends Model {

    private String traceId;
    /**
     * log stage todo
     * 1-1-2
     */
    private String stage;
    private Long happenTime;
    private ProxyProviderModel proxyProviderModel;
    /**
     * Processing node info
     */
    private String node;
    private String logMessage;
}
