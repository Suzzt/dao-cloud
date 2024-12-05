package com.dao.cloud.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2024/12/3 23:40
 * @description:
 */
@Data
@ConfigurationProperties(prefix = "dao-cloud.config")
public class DaoCloudConfigurationProperties {
    private String proxy;
    private String groupId;
}
