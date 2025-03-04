package com.dao.cloud.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * dao cloud provider server config
 *
 * @author sucf
 * @date 2022/12/29 21:30
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "dao-cloud.server")
public class DaoCloudProviderServiceProperties {
    private String proxy;
    private int corePoolSize;
    private int maxPoolSize;
    private int serverPort;
}
