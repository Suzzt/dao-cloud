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

    private static FileSystemSetting fileSystemSetting = new FileSystemSetting();

    private static MysqlSetting mysqlSetting = new MysqlSetting();

    public void setPersistence(String persistence) {
        DaoCloudConfigCenterProperties.persistence = persistence;
    }

    public static String getPersistence() {
        return persistence;
    }

    public void setFileSystemSetting(FileSystemSetting fileSystemSetting) {
        DaoCloudConfigCenterProperties.fileSystemSetting = fileSystemSetting;
    }

    public static FileSystemSetting getFileSystemSetting() {
        return fileSystemSetting;
    }

    public void setMysqlSetting(MysqlSetting mysqlSetting) {
        DaoCloudConfigCenterProperties.mysqlSetting = mysqlSetting;
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

    @Data
    public static class FileSystemSetting {
        private String pathPrefix;
    }
}
