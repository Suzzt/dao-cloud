package com.junmo.center.core.storage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.center.properties.DaoCloudConfigCenterProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.expand.Persistence;
import com.junmo.core.model.*;
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

    private final String config_create_table = "CREATE TABLE IF NOT EXISTS `config` (\n" + "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" + "  `gmt_create` datetime NOT NULL COMMENT '创建时间',\n" + "  `gmt_modified` datetime NOT NULL COMMENT '修改时间',\n" + "  `proxy` varchar(255) NOT NULL COMMENT 'server proxy mark',\n" + "  `key` varchar(255) NOT NULL COMMENT 'key',\n" + "  `version` int(11) NOT NULL COMMENT 'config版本',\n" + "  `value` longtext NOT NULL COMMENT '配置值',\n" + "  PRIMARY KEY (`id`),\n" + "  UNIQUE KEY `config_uk_p_k_v` (`proxy`, `key`, `version`)\n" + ") ENGINE = InnoDB COMMENT '配置中心存储'";

    private final String gateway_limiter_create_table = "CREATE TABLE IF NOT EXISTS `gateway_config`\n" + "(\n" + "    `id`              bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',\n" + "    `gmt_create`      datetime     NOT NULL COMMENT '创建时间',\n" + "    `gmt_modified`    datetime     NOT NULL COMMENT '修改时间',\n" + "    `proxy`           varchar(255) NOT NULL COMMENT 'server proxy mark',\n" + "    `provider`        varchar(255) NOT NULL COMMENT 'service provider',\n" + "    `version`         int(11) NOT NULL COMMENT 'service version',\n" + "    `timeout`         bigint(10)   NOT NULL COMMENT '请求超时时间',\n" + "    `limit_algorithm` int(1)      NOT NULL COMMENT '限流算法: 1=计数, 2=令牌, 3=漏桶',\n" + "    `limit_number`    int(11)      NOT NULL COMMENT '限流数量',\n" + "    PRIMARY KEY (`id`)\n" + ") ENGINE = InnoDB\n" + "  DEFAULT CHARSET = utf8 COMMENT ='网关配置';";

    private final String insert_config_sql_template = "INSERT INTO dao_cloud.config (gmt_create, gmt_modified, proxy, `key`, version, value) VALUES (now(), now(), ?, ?, ?, ?)";

    private final String insert_gateway_sql_template = "INSERT INTO dao_cloud.gateway_config (gmt_create, gmt_modified, proxy, `provider`, version, timeout, limit_algorithm, limit_number) VALUES (now(), now(), ?, ?, ?, ?, ?, ?)";

    private final String update_config_sql_template = "UPDATE dao_cloud.config SET gmt_modified=now(), value=? WHERE proxy=? AND `key`=? AND version=?";

    private final String update_gateway_config_sql_template = "UPDATE dao_cloud.gateway_config SET gmt_modified=now(), timeout=?, limit_algorithm=?, limit_number=? WHERE proxy=? AND `provider`=? AND version=?";

    private final String delete_config_sql_template = "DELETE FROM config WHERE proxy = ? and `key` = ? and value = ?";

    private final String delete_gateway_config_sql_template = "DELETE FROM gateway_config WHERE proxy = ? and `provider` = ? and version = ?";

    @Autowired
    public DbMysql(DaoCloudConfigCenterProperties daoCloudConfigCenterProperties) {
        DaoCloudConfigCenterProperties.MysqlSetting mysqlSetting = daoCloudConfigCenterProperties.getMysqlSetting();
        String url = mysqlSetting.getUrl();
        Integer port = mysqlSetting.getPort();
        String username = mysqlSetting.getUsername();
        String password = mysqlSetting.getPassword();
        if (!StringUtils.hasLength(url) || (port == null || port < 0) || !StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            throw new DaoException(" if configured to persistence = 'mysql', then there must be a mysql parameter.please configure in YAML or properties\n" + " mysql-setting:\n" + "      url: x\n" + "      port: x\n" + "      username: x\n" + "      password: x");
        }
        druidDataSource = new DruidDataSource();
        url = String.format(connect_template, url, port);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(20);
        // 判断下数据库表是否存在,不存在就创建表(config、gateway_config)
        initialize();
    }

    /**
     * init create table
     */
    private void initialize() {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(config_create_table);
            statement.execute(gateway_limiter_create_table);
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
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(delete_config_sql_template)) {
            preparedStatement.setString(1, proxyConfigModel.getProxy());
            preparedStatement.setString(2, proxyConfigModel.getKey());
            preparedStatement.setInt(3, proxyConfigModel.getVersion());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql delete config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    @Override
    public Map<ProxyConfigModel, String> loadConfig() {
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
            List<ConfigPO> configPOList = queryConfigList(i, limit);
            for (ConfigPO configPO : configPOList) {
                ProxyConfigModel proxyConfigModel = new ProxyConfigModel(configPO.getProxy(), configPO.getKey(), configPO.getVersion());
                map.put(proxyConfigModel, configPO.getValue());
            }
        }
        return map;
    }

    @Override
    public void storage(GatewayModel gatewayModel) {
        insertOrUpdate(gatewayModel);
    }

    @Override
    public void delete(ProxyProviderModel proxyProviderModel) {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(delete_gateway_config_sql_template)) {
            preparedStatement.setString(1, proxyProviderModel.getProxy());
            preparedStatement.setString(2, proxyProviderModel.getProviderModel().getProvider());
            preparedStatement.setInt(3, proxyProviderModel.getProviderModel().getVersion());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql delete gateway config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    @Override
    public Map<ProxyProviderModel, GatewayConfigModel> loadGateway() {
        Map<ProxyProviderModel, GatewayConfigModel> map = Maps.newHashMap();
        Long count = count();
        int limit = 500;
        long page = count / limit;
        for (int i = 0; i <= page; i++) {
            List<GatewayConfigPO> gatewayConfigPOList = queryGatewayConfigList(i, limit);
            for (GatewayConfigPO gatewayConfigPO : gatewayConfigPOList) {
                String proxy = gatewayConfigPO.getProxy();
                String provider = gatewayConfigPO.getProvider();
                int version = gatewayConfigPO.getVersion();
                ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, version);
                GatewayConfigModel gatewayConfigModel = new GatewayConfigModel();
                gatewayConfigModel.setTimeout(gatewayConfigPO.getTimeout());
                gatewayConfigModel.setLimitModel(gatewayConfigPO.getLimit());
                map.put(proxyProviderModel, gatewayConfigModel);
            }
        }
        return map;
    }

    private void insertOrUpdate(GatewayModel gatewayModel) {
        ProxyProviderModel proxyProviderModel = gatewayModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select count(1) from gateway_config where proxy=? and `provider`=? and version=?")) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            if (result.getLong(1) == 0) {
                insert(gatewayModel);
            } else {
                update(gatewayModel);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< insertOrUpdate delete config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
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
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insert_config_sql_template)) {
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

    public void insert(GatewayModel gatewayModel) {
        ProxyProviderModel proxyProviderModel = gatewayModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        GatewayConfigModel gatewayConfigModel = gatewayModel.getGatewayConfigModel();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insert_gateway_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            preparedStatement.setLong(4, gatewayConfigModel.getTimeout());
            preparedStatement.setInt(5, gatewayConfigModel.getLimitModel().getLimitAlgorithm());
            preparedStatement.setInt(6, gatewayConfigModel.getLimitModel().getLimitNumber());
            preparedStatement.execute();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql insert gateway_config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    public void update(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String configValue = configModel.getConfigValue();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(update_config_sql_template)) {
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

    public void update(GatewayModel gatewayModel) {
        ProxyProviderModel proxyProviderModel = gatewayModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        GatewayConfigModel gatewayConfigModel = gatewayModel.getGatewayConfigModel();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(update_gateway_config_sql_template)) {
            preparedStatement.setLong(1, gatewayConfigModel.getTimeout());
            preparedStatement.setInt(2, gatewayConfigModel.getLimitModel().getLimitAlgorithm());
            preparedStatement.setInt(3, gatewayConfigModel.getLimitModel().getLimitNumber());
            preparedStatement.setString(4, proxy);
            preparedStatement.setString(5, provider);
            preparedStatement.setInt(6, version);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update gateway config error >>>>>>>>>>>>", e);
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

    private List<ConfigPO> queryConfigList(int index, int size) {
        List<ConfigPO> list = Lists.newArrayList();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from config limit ?, ?");) {
            preparedStatement.setInt(1, index);
            preparedStatement.setInt(2, size);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ConfigPO configPO = configConversion(resultSet);
                list.add(configPO);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
        return list;
    }

    private List<GatewayConfigPO> queryGatewayConfigList(int index, int size) {
        List<GatewayConfigPO> list = Lists.newArrayList();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from gateway_config limit ?, ?");) {
            preparedStatement.setInt(1, index);
            preparedStatement.setInt(2, size);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                GatewayConfigPO gatewayConfigPO = gatewayConfigConversion(resultSet);
                list.add(gatewayConfigPO);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
        return list;
    }

    private ConfigPO configConversion(ResultSet result) throws SQLException {
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

    private GatewayConfigPO gatewayConfigConversion(ResultSet result) throws SQLException {
        GatewayConfigPO gatewayConfigPO = new GatewayConfigPO();
        gatewayConfigPO.setId(result.getLong("id"));
        gatewayConfigPO.setCreateTime(result.getDate("gmt_create"));
        gatewayConfigPO.setUpdateTime(result.getDate("gmt_modified"));
        gatewayConfigPO.setProxy(result.getString("proxy"));
        gatewayConfigPO.setProvider(result.getString("provider"));
        gatewayConfigPO.setVersion(result.getInt("version"));
        LimitModel limitModel = new LimitModel(result.getInt("limit_algorithm"), result.getInt("limit_number"));
        gatewayConfigPO.setLimit(limitModel);
        return gatewayConfigPO;
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

    @Data
    private class GatewayConfigPO {
        private Long id;
        private Date createTime;
        private Date updateTime;
        private String proxy;
        private String provider;
        private int version;
        private Long timeout;
        private LimitModel limit;
    }
}
