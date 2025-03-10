package com.dao.cloud.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author oooootemp
 * @since 1.0.0
 * @date 2024/4/5 11:31
 *
 * dao cloud gateway configuration
 */
@Data
@ConfigurationProperties(prefix = "dao-cloud.gateway")
public class DaoCloudGatewayProperties {
    private Integer version;
    private String loadBalance;
}
