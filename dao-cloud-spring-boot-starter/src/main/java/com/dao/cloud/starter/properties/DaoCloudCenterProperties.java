package com.dao.cloud.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/6/10 15:52
 * @description: dao cloud center configuration
 */
@ConfigurationProperties(prefix = "dao-cloud.center")
public class DaoCloudCenterProperties {

    public static String ip;

    public void setIp(String ip) {
        DaoCloudCenterProperties.ip = ip;
    }
}
