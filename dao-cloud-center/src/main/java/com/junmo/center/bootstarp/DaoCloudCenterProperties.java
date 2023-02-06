package com.junmo.center.bootstarp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/2/6 21:42
 * @description:
 */
@ConfigurationProperties(prefix = "dao-cloud")
public class DaoCloudCenterProperties {
    public static String serializer;

    public void setSerializer(String serializer) {
        DaoCloudCenterProperties.serializer = serializer;
    }
}
