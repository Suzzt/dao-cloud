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

    private FileSystemSetting fileSystemSetting = new FileSystemSetting();

    private MysqlSetting mysqlSetting = new MysqlSetting();

    public FileSystemSetting getFileSystemSetting() {
        return fileSystemSetting;
    }

    public void setFileSystemSetting(FileSystemSetting fileSystemSetting) {
        this.fileSystemSetting = fileSystemSetting;
    }

    public MysqlSetting getMysqlSetting() {
        return mysqlSetting;
    }

    public void setMysqlSetting(MysqlSetting mysqlSetting) {
        this.mysqlSetting = mysqlSetting;
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
