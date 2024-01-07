package com.junmo.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2024/1/2 17:44
 * @description: 网关参数配置
 */
@ConfigurationProperties(prefix = "dao-cloud.gateway")
public class GatewayProperties {
    public static int corePoolSize;
    public static int maxPoolSize;

    public static int getCorePoolSize() {
        return corePoolSize;
    }

    public static void setCorePoolSize(int corePoolSize) {
        GatewayProperties.corePoolSize = corePoolSize;
    }

    public static int getMaxPoolSize() {
        return maxPoolSize;
    }

    public static void setMaxPoolSize(int maxPoolSize) {
        GatewayProperties.maxPoolSize = maxPoolSize;
    }
}
