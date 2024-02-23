package com.junmo.center.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/3/12 17:58
 * @description:
 */
@ConfigurationProperties(prefix = "dao-cloud.center.cluster")
public class DaoCloudClusterCenterProperties {
    public static String ip;

    public void setIp(String ip) {
        DaoCloudClusterCenterProperties.ip = ip;
    }
}
