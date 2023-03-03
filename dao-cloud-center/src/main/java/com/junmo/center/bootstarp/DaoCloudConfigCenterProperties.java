package com.junmo.center.bootstarp;

import lombok.Data;
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

    private static MysqlSetting mysqlSetting = new MysqlSetting();

    public void setPersistence(String persistence) {
        DaoCloudConfigCenterProperties.persistence = persistence;
    }

    public void setPrefix(String prefix) {
        DaoCloudConfigCenterProperties.prefix = prefix;
    }

    public void setMysqlSetting(MysqlSetting mysqlSetting) {
        DaoCloudConfigCenterProperties.mysqlSetting = mysqlSetting;
    }

    public static String getPersistence() {
        return persistence;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static MysqlSetting getMysqlSetting() {
        return mysqlSetting;
    }

    @Data
    public static class MysqlSetting {
        private String url;
        private Integer port;
        private String username;
        private String password;
    }
}
