package com.junmo.boot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/6/10 15:52
 * @description: dao cloud center config
 */
@ConfigurationProperties(prefix = "dao-cloud.center")
public class DaoCloudCenterProperties {
    private static String dashboard;
    private static String cluster;
}
