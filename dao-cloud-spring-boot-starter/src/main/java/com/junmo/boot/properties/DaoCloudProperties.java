package com.junmo.boot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2022/12/29 21:30
 * @description: dao cloud config
 */
@ConfigurationProperties(prefix = "dao-cloud")
public class DaoCloudProperties {
    public static String proxy;
    public static int corePoolSize;
    public static int maxPoolSize;
    public static int serverPort;
    // form yaml
    public static String serializer;
    // use inner
    public static byte serializerType;

    public void setProxy(String proxy) {
        DaoCloudProperties.proxy = proxy;
    }

    public void setCorePoolSize(int corePoolSize) {
        DaoCloudProperties.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        DaoCloudProperties.maxPoolSize = maxPoolSize;
    }

    public void setServerPort(int serverPort) {
        DaoCloudProperties.serverPort = serverPort;
    }

    public void setSerializer(String serializer) {
        DaoCloudProperties.serializer = serializer;
    }
}
