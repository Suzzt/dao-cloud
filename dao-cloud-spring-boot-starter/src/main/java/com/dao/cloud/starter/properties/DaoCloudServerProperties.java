package com.dao.cloud.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2022/12/29 21:30
 * @description: dao cloud server config
 */
@ConfigurationProperties(prefix = "dao-cloud.server")
public class DaoCloudServerProperties {
    public static String proxy;
    public static int corePoolSize;
    public static int maxPoolSize;
    public static int serverPort;

    public void setProxy(String proxy) {
        DaoCloudServerProperties.proxy = proxy;
    }

    public void setCorePoolSize(int corePoolSize) {
        DaoCloudServerProperties.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        DaoCloudServerProperties.maxPoolSize = maxPoolSize;
    }

    public void setServerPort(int serverPort) {
        DaoCloudServerProperties.serverPort = serverPort;
    }
}
