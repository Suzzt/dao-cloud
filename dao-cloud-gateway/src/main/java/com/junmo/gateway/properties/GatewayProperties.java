package com.junmo.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2024/1/2 17:44
 * @description: 网关参数配置
 */
@ConfigurationProperties(prefix = "dao-cloud.gateway")
public class GatewayProperties {
    public int corePoolSize;
    public int maxPoolSize;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
