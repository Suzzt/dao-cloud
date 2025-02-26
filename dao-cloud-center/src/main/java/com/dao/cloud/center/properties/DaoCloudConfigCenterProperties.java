package com.dao.cloud.center.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sucf
 * @since 1.0
 */
@ConfigurationProperties(prefix = "dao-cloud.center.storage")
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
