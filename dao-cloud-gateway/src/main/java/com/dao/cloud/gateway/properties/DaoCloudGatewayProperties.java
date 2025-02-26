package com.dao.cloud.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author oooootemp
 * @date: 2024/4/5 11:31
 * @description: dao cloud gateway configuration
 */
@ConfigurationProperties(prefix = "dao-cloud.gateway")
public class DaoCloudGatewayProperties {

    public static Integer version;

    public static String loadBalance;

    public void setVersion(Integer version) {
        DaoCloudGatewayProperties.version = version;
    }

    public void setLoadBalance(String loadBalance) {
        DaoCloudGatewayProperties.loadBalance = loadBalance;
    }
}
