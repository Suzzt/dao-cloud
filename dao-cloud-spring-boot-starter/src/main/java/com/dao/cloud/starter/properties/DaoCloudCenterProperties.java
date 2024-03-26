package com.dao.cloud.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/6/10 15:52
 * @description: dao cloud center config
 */
@ConfigurationProperties(prefix = "dao-cloud.gateway.center")
public class DaoCloudCenterProperties {

    public static String ip;

    public static String loadBalance;

    public void setIp(String ip) {
        DaoCloudCenterProperties.ip = ip;
    }

    public void setLoadBalance(String loadBalance) {
        DaoCloudCenterProperties.loadBalance = loadBalance;
    }
}
