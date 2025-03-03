package com.dao.cloud.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/12/29 21:30
 * dao cloud server config
 */
@ConfigurationProperties(prefix = "dao-cloud.server")
public class DaoCloudProviderServiceProperties {
    public static String proxy;
    public static int corePoolSize;
    public static int maxPoolSize;
    public static int serverPort;

    public void setProxy(String proxy) {
        DaoCloudProviderServiceProperties.proxy = proxy;
    }

    public void setCorePoolSize(int corePoolSize) {
        DaoCloudProviderServiceProperties.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        DaoCloudProviderServiceProperties.maxPoolSize = maxPoolSize;
    }

    public void setServerPort(int serverPort) {
        DaoCloudProviderServiceProperties.serverPort = serverPort;
    }
}
