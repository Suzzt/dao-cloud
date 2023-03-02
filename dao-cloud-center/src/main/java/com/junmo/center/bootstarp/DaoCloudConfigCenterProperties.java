package com.junmo.center.bootstarp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/2/6 21:42
 * @description:
 */
@ConfigurationProperties(prefix = "dao-cloud.config")
public class DaoCloudConfigCenterProperties {
    private static String persistence;

    private static String prefix;

    public static String getPersistence() {
        return persistence;
    }

    public static String getPrefix() {
        return prefix;
    }

    public void setPersistence(String persistence) {
        DaoCloudConfigCenterProperties.persistence = persistence;
    }

    public void setPrefix(String prefix) {
        DaoCloudConfigCenterProperties.prefix = prefix;
    }
}
