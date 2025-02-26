package com.dao.cloud.center.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sucf
 * @since 1.0
 */
@ConfigurationProperties(prefix = "dao-cloud.center.cluster")
public class DaoCloudClusterCenterProperties {
    public static String ip;

    public void setIp(String ip) {
        DaoCloudClusterCenterProperties.ip = ip;
    }
}
