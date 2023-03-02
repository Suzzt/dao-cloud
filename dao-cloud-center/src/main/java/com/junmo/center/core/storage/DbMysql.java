package com.junmo.center.core.storage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.Data;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: sucf
 * @date: 2023/2/28 23:45
 * @description: data in mysql persistence
 */
public class DbMysql {
    private final String connect_template = "jdbc:mysql://%s:%s/dao_cloud?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";

    private final String driver = "com.mysql.cj.jdbc.Driver";

    private DruidDataSource druidDataSource;

    private DbMysql(String url, int port, String username, String password) {
        druidDataSource = new DruidDataSource();
        url = String.format(connect_template, url, port);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(20);
    }

    private ConfigPO query(String proxy, String key, int value) throws SQLException {
        DruidPooledConnection connection = druidDataSource.getConnection();
        String sql = "select * from config where proxy = '%s' and `key` = '%s' and value = %s";
        ResultSet result = connection.createStatement().executeQuery(String.format(sql, proxy, key, value));
        if (result.next()) {
            ConfigPO configPO = new ConfigPO();
            configPO.setId(result.getLong("id"));
            configPO.setCreateTime(result.getDate("gmt_create"));
            configPO.setUpdateTime(result.getDate("gmt_modified"));
            configPO.setProxy(result.getString("proxy"));
            configPO.setKey(result.getString("key"));
            configPO.setVersion(result.getInt("version"));
            configPO.setValue(result.getString("value"));
            return configPO;
        } else {
            return null;
        }
    }

    @Data
    private class ConfigPO {
        private Long id;
        private Date createTime;
        private Date updateTime;
        private String proxy;
        private String key;
        private int version;
        private String value;
    }
}
