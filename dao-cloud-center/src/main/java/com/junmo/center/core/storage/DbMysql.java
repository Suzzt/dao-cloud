package com.junmo.center.core.storage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/2/28 23:45
 * @description: data in mysql persistence
 */
@Slf4j
public class DbMysql implements Persistence {
    private final String connect_template = "jdbc:mysql://%s:%s/dao_cloud?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";

    private final String driver = "com.mysql.cj.jdbc.Driver";

    private DruidDataSource druidDataSource;

    private final String create_table = "CREATE TABLE IF NOT EXISTS `dao_cloud.config` ( `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键', `gmt_create` datetime NOT NULL COMMENT '创建时间', `gmt_modified` datetime NOT NULL COMMENT '修改时间', `proxy` varchar(255) NOT NULL COMMENT 'server proxy mark', `key` varchar(255) NOT NULL COMMENT 'key', `version` int(11) NOT NULL COMMENT 'config版本', `value` longtext NOT NULL COMMENT '配置值', PRIMARY KEY (`id`), UNIQUE KEY `config_uk_p_k_v` (`proxy`, `key`, `version`) ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET = utf8 COMMENT '配置中心存储'";

    private final String example_data = "INSERT INTO `dao_cloud.config` (gmt_create, gmt_modified, proxy, `key`, version , value) VALUES (now(), now(), 'dao-cloud', 'dao-cloud', 0 , 'Welcome to dao-cloud!')";

    public DbMysql(String url, int port, String username, String password) {
        druidDataSource = new DruidDataSource();
        url = String.format(connect_template, url, port);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(20);
    }

    @Override
    public void storage(ConfigModel configModel) {
        insert(configModel);
    }

    @Override
    public void delete(ProxyConfigModel proxyConfigModel) {
        delete(proxyConfigModel.getProxy(), proxyConfigModel.getKey(), proxyConfigModel.getVersion());
    }

    @Override
    public String getValue(ProxyConfigModel proxyConfigModel) {
        return null;
    }

    @Override
    public Map<ProxyConfigModel, String> load() {
        // 判断下数据库表是否存在,存在就载入配置数据,不存在就创建表
        initialize();
        Map<ProxyConfigModel, String> map = Maps.newConcurrentMap();
        Long count = count();
        if (count == 0) {
            initialize();
            count = count();
        }
        int limit = 500;
        long page = count / limit;
        for (int i = 0; i <= page; i++) {
            List<ConfigPO> configPOList = queryList(i, limit);
            for (ConfigPO configPO : configPOList) {
                ProxyConfigModel proxyConfigModel = new ProxyConfigModel(configPO.getProxy(), configPO.getKey(), configPO.getVersion());
                map.put(proxyConfigModel, configPO.getValue());
            }
        }
        return map;
    }

    /**
     * init table
     */
    private void create() {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(create_table);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * init example data
     */
    private void initialize() {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(example_data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String configValue = configModel.getConfigValue();
        try (DruidPooledConnection connection = druidDataSource.getConnection()) {
            String sql = "INSERT INTO dao_cloud.config (gmt_create, gmt_modified, proxy, `key`, version, value) VALUES (now(), now(), '%s', '%s', %s, '%s')";
            connection.createStatement().execute(String.format(sql, proxy, key, version, configValue));
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql delete config error >>>>>>>>>>>>", e);
            throw new RuntimeException(e);
        }
    }

    private void delete(String proxy, String key, int value) {
        try (DruidPooledConnection connection = druidDataSource.getConnection()) {
            String sql = "delete where proxy = '%s' and `key` = '%s' and value = %s";
            connection.createStatement().execute(String.format(sql, proxy, key, value));
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql delete config error >>>>>>>>>>>>", e);
            throw new RuntimeException(e);
        }
    }

    private Long count() {
        try (DruidPooledConnection connection = druidDataSource.getConnection()) {
            String sql = "select count(1) from config";
            ResultSet result = connection.createStatement().executeQuery(sql);
            result.next();
            return result.getLong(1);
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql count config error >>>>>>>>>>>>", e);
            throw new RuntimeException(e);
        }
    }

    private List<ConfigPO> queryList(int index, int size) {
        List<ConfigPO> list = Lists.newArrayList();
        try (DruidPooledConnection connection = druidDataSource.getConnection()) {
            String sql = "select * from config limit %s, %s";
            ResultSet result = connection.createStatement().executeQuery(String.format(sql, index, size));
            if (result.next()) {
                ConfigPO configPO = conversion(result);
                list.add(configPO);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query config error >>>>>>>>>>>>", e);
            throw new RuntimeException(e);
        }
        return list;
    }

    private ConfigPO conversion(ResultSet result) throws SQLException {
        ConfigPO configPO = new ConfigPO();
        configPO.setId(result.getLong("id"));
        configPO.setCreateTime(result.getDate("gmt_create"));
        configPO.setUpdateTime(result.getDate("gmt_modified"));
        configPO.setProxy(result.getString("proxy"));
        configPO.setKey(result.getString("key"));
        configPO.setVersion(result.getInt("version"));
        configPO.setValue(result.getString("value"));
        return configPO;
    }

    private ConfigPO query(String proxy, String key, int value) throws SQLException {
        try (DruidPooledConnection connection = druidDataSource.getConnection()) {
            String sql = "select * from config where proxy = '%s' and `key` = '%s' and value = %s";
            ResultSet result = connection.createStatement().executeQuery(String.format(sql, proxy, key, value));
            if (result.next()) {
                ConfigPO configPO = conversion(result);
                return configPO;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query config error >>>>>>>>>>>>", e);
            throw e;
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
