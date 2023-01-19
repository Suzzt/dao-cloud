package com.junmo.boot.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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

    public static String getProxy() {
        return proxy;
    }

    public static int getCorePoolSize() {
        return corePoolSize;
    }

    public static int getMaxPoolSize() {
        return maxPoolSize;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String getSerializer() {
        return serializer;
    }

    public static byte getSerializerType() {
        return serializerType;
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
