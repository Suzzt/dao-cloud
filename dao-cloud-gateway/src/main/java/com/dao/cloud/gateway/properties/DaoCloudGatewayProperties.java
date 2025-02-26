package com.dao.cloud.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author oooootemp
 * @since 1.0
 * dao cloud gateway configuration
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
