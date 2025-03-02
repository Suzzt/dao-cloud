package com.dao.cloud.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/6/10 15:52
 * dao cloud center configuration
 */
@ConfigurationProperties(prefix = "dao-cloud.center")
public class DaoCloudCenterProperties {

    public static String ip;

    public void setIp(String ip) {
        DaoCloudCenterProperties.ip = ip;
    }
}
