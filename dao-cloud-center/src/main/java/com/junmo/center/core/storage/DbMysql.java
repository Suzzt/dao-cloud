package com.junmo.center.core.storage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.center.bootstarp.DaoCloudConfigCenterProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.expand.Persistence;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.GatewayModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.model.ProxyProviderModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/2/28 23:45
 * @description: data in mysql persistence
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "dao-cloud.center.storage.way", havingValue = "mysql")
public class DbMysql implements Persistence {

    private final String connect_template = "jdbc:mysql://%s:%s/dao_cloud?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";

    private final String driver = "com.mysql.cj.jdbc.Driver";

    private DruidDataSource druidDataSource;

    private final String create_table = "CREATE TABLE IF NOT EXISTS `config` ( `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键', `gmt_create` datetime NOT NULL COMMENT '创建时间', `gmt_modified` datetime NOT NULL COMMENT '修改时间', `proxy` varchar(255) NOT NULL COMMENT 'server proxy mark', `key` varchar(255) NOT NULL COMMENT 'key', `version` int(11) NOT NULL COMMENT 'config版本', `value` longtext NOT NULL COMMENT '配置值', PRIMARY KEY (`id`), UNIQUE KEY `config_uk_p_k_v` (`proxy`, `key`, `version`) ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARSET = utf8 COMMENT '配置中心存储'";

    private final String insert_sql_template = "INSERT INTO dao_cloud.config (gmt_create, gmt_modified, proxy, `key`, version, value) VALUES (now(), now(), ?, ?, ?, ?)";

    private final String update_sql_template = "UPDATE dao_cloud.config SET gmt_modified=now(), value=? WHERE proxy=? AND `key`=? AND version=?";

    private final String delete_sql_template = "DELETE FROM config WHERE PROXY = ? and `key` = ? and value = ?";

    @Autowired
    public DbMysql(DaoCloudConfigCenterProperties daoCloudConfigCenterProperties) {
        DaoCloudConfigCenterProperties.MysqlSetting mysqlSetting = daoCloudConfigCenterProperties.getMysqlSetting();
        String url = mysqlSetting.getUrl();
        Integer port = mysqlSetting.getPort();
        String username = mysqlSetting.getUsername();
        String password = mysqlSetting.getPassword();
        if (!StringUtils.hasLength(url) || (port == null || port < 0)
                || !StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            throw new DaoException(" if configured to persistence = 'mysql', then there must be a mysql parameter.please configure in YAML or properties\n" + " mysql-setting:\n" + "      url: x\n" + "      port: x\n" + "      username: x\n" + "      password: x");
        }
        druidDataSource = new DruidDataSource();
        url = String.format(connect_template, url, port);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(20);
    }

    /**
     * init create table
     */
    private void initialize() {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(create_table);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void storage(ConfigModel configModel) {
        insertOrUpdate(configModel);
    }

    @Override
    public void delete(ProxyConfigModel proxyConfigModel) {
        delete(proxyConfigModel.getProxy(), proxyConfigModel.getKey(), proxyConfigModel.getVersion());
    }

    @Override
    public Map<ProxyConfigModel, String> load() {
        // 判断下数据库表是否存在,存在就载入配置数据,不存在就创建表
        initialize();
        Map<ProxyConfigModel, String> map = Maps.newHashMap();
        Long count = count();
        if (count == 0) {
            // init example data
            ConfigModel configModel = new ConfigModel();
            ProxyConfigModel proxyConfigModel = new ProxyConfigModel("dao-cloud", "dao-cloud", 0);
            configModel.setProxyConfigModel(proxyConfigModel);
            configModel.setConfigValue("Welcome to dao-cloud!");
            insert(configModel);
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

    @Override
    public void storage(GatewayModel gatewayModel) {

    }

    @Override
    public void delete(ProxyProviderModel proxyProviderModel) {

    }

    private void insertOrUpdate(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select count(1) from config where proxy=? and `key`=? and version=?")) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, key);
            preparedStatement.setInt(3, version);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            if (result.getLong(1) == 0) {
                insert(configModel);
            } else {
                update(configModel);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< insertOrUpdate delete config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    public void insert(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String configValue = configModel.getConfigValue();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insert_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, key);
            preparedStatement.setInt(3, version);
            preparedStatement.setString(4, configValue);
            preparedStatement.execute();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql insert config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    public void update(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String configValue = configModel.getConfigValue();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(update_sql_template)) {
            preparedStatement.setString(1, configValue);
            preparedStatement.setString(2, proxy);
            preparedStatement.setString(3, key);
            preparedStatement.setInt(4, version);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    private void delete(String proxy, String key, int version) {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(delete_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, key);
            preparedStatement.setInt(3, version);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql delete config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
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
            throw new DaoException(e);
        }
    }

    private List<ConfigPO> queryList(int index, int size) {
        List<ConfigPO> list = Lists.newArrayList();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from config limit ?, ?");) {
            preparedStatement.setInt(1, index);
            preparedStatement.setInt(2, size);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ConfigPO configPO = conversion(resultSet);
                list.add(configPO);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
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

    private ConfigPO query(String proxy, String key, int value) {
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
            throw new DaoException(e);
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
