package com.dao.cloud.center.core.storage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.dao.cloud.center.core.model.ConfigurationProperty;
import com.dao.cloud.center.core.model.ServerProxyProviderNode;
import com.dao.cloud.center.properties.DaoCloudConfigCenterProperties;
import com.dao.cloud.center.web.vo.CallTrendVO;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.*;
import java.util.*;

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

    private final String config_create_table_sql = "CREATE TABLE IF NOT EXISTS `config`\n" +
            "(\n" +
            "    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "    `gmt_create`   datetime     NOT NULL COMMENT '创建时间',\n" +
            "    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',\n" +
            "    `proxy`        varchar(255) NOT NULL COMMENT 'server proxy mark',\n" +
            "    `key`          varchar(255) NOT NULL COMMENT 'key',\n" +
            "    `version`      int(11)      NOT NULL COMMENT 'config版本',\n" +
            "    `value`        longtext     NOT NULL COMMENT '配置值',\n" +
            "    PRIMARY KEY (`id`),\n" +
            "    UNIQUE KEY `config_uk_p_k_v` (`proxy`, `key`, `version`)\n" +
            ") ENGINE = InnoDB\n" +
            "  AUTO_INCREMENT = 1\n" +
            "  DEFAULT CHARSET = utf8 COMMENT ='配置中心存储内容表'";

    private final String gateway_config_create_table_sql = "CREATE TABLE IF NOT EXISTS `gateway_config`\n" +
            "(\n" +
            "    `id`                             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',\n" +
            "    `gmt_create`                     datetime     NOT NULL COMMENT '创建时间',\n" +
            "    `gmt_modified`                   datetime     NOT NULL COMMENT '修改时间',\n" +
            "    `proxy`                          varchar(255) NOT NULL COMMENT 'server proxy mark',\n" +
            "    `provider`                       varchar(255) NOT NULL COMMENT 'service provider',\n" +
            "    `version`                        int(11)      NOT NULL COMMENT 'service version',\n" +
            "    `timeout`                        bigint(10) COMMENT '请求超时时间',\n" +
            "    `limit_algorithm`                int(1) COMMENT '限流算法: 1=计数, 2=令牌, 3=漏桶',\n" +
            "    `slide_date_window_size`         bigint(20) COMMENT '滑动时间窗口大小(ms)',\n" +
            "    `slide_window_max_request_count` int(11) COMMENT '时间窗口内允许的最大请求数',\n" +
            "    `token_bucket_max_size`          int(11) COMMENT '令牌桶的最大令牌数',\n" +
            "    `token_bucket_refill_rate`       int(11) COMMENT '每秒新增的令牌数',\n" +
            "    `leaky_bucket_capacity`          int(11) COMMENT '漏桶的容量',\n" +
            "    `leaky_bucket_refill_rate`       int(11) COMMENT '漏桶令牌填充的速度(每秒)',\n" +
            "    PRIMARY KEY (`id`)\n" +
            ") ENGINE = InnoDB\n" +
            "  DEFAULT CHARSET = utf8 COMMENT ='网关配置';";

    private final String server_config_create_table_sql = "CREATE TABLE IF NOT EXISTS `server_config`\n" +
            "(\n" +
            "    `id`                             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',\n" +
            "    `gmt_create`                     datetime     NOT NULL COMMENT '创建时间',\n" +
            "    `gmt_modified`                   datetime     NOT NULL COMMENT '修改时间',\n" +
            "    `proxy`                          varchar(255) NOT NULL COMMENT 'server proxy mark',\n" +
            "    `provider`                       varchar(255) NOT NULL COMMENT 'service provider',\n" +
            "    `version`                        int(11)      NOT NULL COMMENT 'service version',\n" +
            "    `ip`                             varchar(20)  NOT NULL COMMENT 'ip',\n" +
            "    `port`                           int(10) COMMENT 'port',\n" +
            "    `status`                         tinyint COMMENT 'server status',\n" +
            "    PRIMARY KEY (`id`)\n" +
            ") ENGINE = InnoDB\n" +
            "  DEFAULT CHARSET = utf8 COMMENT ='服务配置';";

    private final String call_trend_create_table_sql = "CREATE TABLE IF NOT EXISTS `call_trend`\n" +
            "(\n" +
            "    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "    `gmt_create`   datetime     NOT NULL COMMENT '创建时间',\n" +
            "    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',\n" +
            "    `proxy`        varchar(255) NOT NULL COMMENT 'server proxy mark',\n" +
            "    `provider`     varchar(255) NOT NULL COMMENT 'provider',\n" +
            "    `version`      int(11)      NOT NULL COMMENT '版本',\n" +
            "    `method_name`  varchar(512) NOT NULL COMMENT '函数方法名',\n" +
            "    `count`        bigint(20)   NOT NULL COMMENT '计数值',\n" +
            "    PRIMARY KEY (`id`),\n" +
            "    UNIQUE KEY `config_uk_p_p_v_m` (`proxy`, `provider`, `version`, `method_name`)\n" +
            ") ENGINE = InnoDB\n" +
            "  AUTO_INCREMENT = 1\n" +
            "  DEFAULT CHARSET = utf8 COMMENT ='接口调用趋势表';";

    private final String insert_config_sql_template = "INSERT INTO dao_cloud.config (gmt_create, gmt_modified, proxy, `key`, version, value) VALUES (now(), now(), ?, ?, ?, ?)";

    private final String insert_gateway_sql_template = "INSERT INTO dao_cloud.gateway_config (gmt_create, gmt_modified, proxy, `provider`, version, timeout, limit_algorithm, slide_date_window_size, slide_window_max_request_count, token_bucket_max_size, token_bucket_refill_rate, leaky_bucket_capacity, leaky_bucket_refill_rate) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String insert_server_sql_template = "INSERT INTO dao_cloud.server_config (gmt_create, gmt_modified, proxy, `provider`, version, ip, port, `status`) VALUES (now(), now(), ?, ?, ?, ?, ?, ?)";

    private final String insert_call_trend_sql_template = "INSERT INTO dao_cloud.call_trend (gmt_create, gmt_modified, proxy, `provider`, version, method_name, count) VALUES (now(), now(), ?, ?, ?, ?, ?)";

    private final String update_config_sql_template = "UPDATE dao_cloud.config SET gmt_modified=now(), value=? WHERE proxy=? AND `key`=? AND version=?";

    private final String update_gateway_config_sql_template = "UPDATE dao_cloud.gateway_config SET gmt_modified=now(), timeout=?, limit_algorithm=?, slide_date_window_size=?, slide_window_max_request_count=?, token_bucket_max_size=?, token_bucket_refill_rate=?, leaky_bucket_capacity=?, leaky_bucket_refill_rate=? WHERE proxy=? AND `provider`=? AND version=?";

    private final String update_server_config_sql_template = "UPDATE dao_cloud.server_config SET gmt_modified=now(), `status`=? WHERE proxy=? AND `provider`=? AND version=? AND ip=? AND port=?";

    private final String update_call_trend_sql_template = "UPDATE dao_cloud.call_trend SET gmt_modified=now(), `count` = `count` + ? WHERE proxy=? AND `provider`=? AND version=? AND method_name=?";

    private final String delete_config_sql_template = "DELETE FROM dao_cloud.config WHERE proxy = ? and `key` = ? and value = ?";

    private final String delete_gateway_config_sql_template = "DELETE FROM dao_cloud.gateway_config WHERE proxy = ? and `provider` = ? and version = ?";

    private final String truncate_config_sql_template = "TRUNCATE TABLE dao_cloud.config";

    private final String truncate_gateway_config_sql_template = "TRUNCATE TABLE dao_cloud.gateway_config";

    private final String truncate_server_config_sql_template = "TRUNCATE TABLE dao_cloud.server_config";

    private final String truncate_call_trend_sql_template = "TRUNCATE TABLE dao_cloud.call_trend";

    private final String select_call_trend_sql_template = "SELECT method_name, `count` as c from dao_cloud.call_trend WHERE proxy=? AND `provider`=? AND version=?";
    private final String select_all_call_trend_sql_template = "SELECT * from dao_cloud.call_trend limit ?,?";

    private final String delete_call_trend_sql_template = "delete from dao_cloud.call_trend WHERE proxy=? AND `provider`=? AND version=?";
    private final String delete_call_trend_by_method_sql_template = "delete from dao_cloud.call_trend WHERE proxy=? AND `provider`=? AND version=? AND method_name=?";

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
        // 判断下数据库表是否存在,不存在就创建表(config、gateway_config、server_config、call_trend)
        initialize();
    }

    /**
     * init create table
     */
    private void initialize() {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(config_create_table_sql);
            statement.execute(gateway_config_create_table_sql);
            statement.execute(server_config_create_table_sql);
            statement.execute(call_trend_create_table_sql);
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
    public void delete(ConfigurationProperty configurationProperty) {

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
    public void storage(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        insertOrUpdate(proxyProviderModel, serverNodeModel);
    }

    @Override
    public void storage(ConfigurationProperty configurationProperty) {

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

    @Override
    public Map<ServerProxyProviderNode, Boolean> loadServer() {
        Map<ServerProxyProviderNode, Boolean> map = Maps.newHashMap();
        Long count = count();
        int limit = 500;
        long page = count / limit;
        for (int i = 0; i <= page; i++) {
            List<ServerConfigPO> serverConfigPOList = queryServerConfigList(i, limit);
            for (ServerConfigPO serverConfigPO : serverConfigPOList) {
                String proxy = serverConfigPO.getProxy();
                String provider = serverConfigPO.getProvider();
                int version = serverConfigPO.getVersion();
                String ip = serverConfigPO.getIp();
                int port = serverConfigPO.getPort();
                boolean status = serverConfigPO.isStatus();
                ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, version);
                ServerProxyProviderNode serverProxyProviderNode = new ServerProxyProviderNode(proxyProviderModel, ip, port);
                map.put(serverProxyProviderNode, status);
            }
        }
        return map;
    }

    @Override
    public Set<String> getConfigurationFile(String proxy, String groupId) {
        return Collections.emptySet();
    }

    @Override
    public String getConfigurationProperty(String proxy, String groupId, String fileName) {
        return "";
    }

    @Override
    public void clear() {
        try (DruidPooledConnection connection = druidDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(truncate_config_sql_template);
            statement.execute(truncate_gateway_config_sql_template);
            statement.execute(truncate_server_config_sql_template);
            statement.execute(truncate_call_trend_sql_template);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void callTrendIncrement(CallTrendModel callTrendModel) {
        Long count = callTrendModel.getCount();
        if (Objects.isNull(count) || count == 0L) {
            return;
        }
        insertOrUpdate(callTrendModel);
    }

    @Override
    public List<CallTrendVO> getCallCount(ProxyProviderModel proxyProviderModel) {
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(select_call_trend_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<CallTrendVO> callTrendVOS = new ArrayList<>();
            while (resultSet.next()) {
                String methodName = resultSet.getString("method_name");
                Long count = resultSet.getLong("c");
                CallTrendVO callTrendVO = new CallTrendVO(methodName, count);
                callTrendVOS.add(callTrendVO);
            }
            return callTrendVOS;
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query call trend error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    @Override
    public void callTrendClear(ProxyProviderModel proxyProviderModel, String methodName) {
        this.doCallTrendClear(proxyProviderModel, methodName);
    }

    @Override
    public List<CallTrendModel> getCallTrends() {
        int pageNum = 1;
        int pageSize = 1000;
        List<CallTrendModel> callTrendModels = new ArrayList<>();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(select_all_call_trend_sql_template)) {
            while (true) {
                boolean isBreak = true;
                preparedStatement.setInt(1, (pageNum - 1) * pageSize);
                preparedStatement.setInt(2, pageSize);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        isBreak = false;
                        String provider = resultSet.getString("provider");
                        String proxy = resultSet.getString("proxy");
                        String method = resultSet.getString("method_name");
                        Integer version = resultSet.getInt("version");
                        Long count = resultSet.getLong("count");
                        ProviderModel providerModel = new ProviderModel(provider, version);
                        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, providerModel);
                        CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, method, Long.valueOf(count));
                        callTrendModels.add(callTrendModel);
                    }
                }
                if (isBreak) {
                    break;
                }
                pageNum += 1;
            }
            return callTrendModels;
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update call trend error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }

    }

    @Override
    public void storage(LogModel logModel) {

    }

    private void doCallTrendClear(ProxyProviderModel proxyProviderModel, String methodName) {

        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
                StringUtils.hasLength(methodName)
                        ? delete_call_trend_by_method_sql_template
                        : delete_call_trend_sql_template
        )) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            if (StringUtils.hasLength(methodName)) {
                preparedStatement.setString(4, methodName);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update call trend error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
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
            log.error("<<<<<<<<<<<< insertOrUpdate gateway config error >>>>>>>>>>>>", e);
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
            log.error("<<<<<<<<<<<< insertOrUpdate config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    private void insertOrUpdate(CallTrendModel callTrendModel) {
        ProxyProviderModel proxyProviderModel = callTrendModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(select_call_trend_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                long count = result.getLong(2);
                callTrendModel.setCount(count);
                update(callTrendModel);
            } else {
                insert(callTrendModel);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< insertOrUpdate config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    private void insertOrUpdate(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select count(1) from server_config where proxy=? and `provider`=? and version=? and ip=? and port=?")) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            preparedStatement.setString(4, serverNodeModel.getIp());
            preparedStatement.setInt(5, serverNodeModel.getPort());
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            if (result.getLong(1) == 0) {
                insert(proxyProviderModel, serverNodeModel);
            } else {
                update(proxyProviderModel, serverNodeModel);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< insertOrUpdate server config error >>>>>>>>>>>>", e);
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

    public void insert(CallTrendModel callTrendModel) {
        ProxyProviderModel proxyProviderModel = callTrendModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        String methodName = callTrendModel.getMethodName();
        Long count = callTrendModel.getCount();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insert_call_trend_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            preparedStatement.setString(4, methodName);
            preparedStatement.setLong(5, count);
            preparedStatement.execute();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql insert call trend error >>>>>>>>>>>>", e);
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
            preparedStatement.setObject(4, gatewayConfigModel.getTimeout());
            preparedStatement.setInt(5, gatewayConfigModel.getLimitModel().getLimitAlgorithm());
            preparedStatement.setObject(6, gatewayConfigModel.getLimitModel().getSlideDateWindowSize());
            preparedStatement.setObject(7, gatewayConfigModel.getLimitModel().getSlideWindowMaxRequestCount());
            preparedStatement.setObject(8, gatewayConfigModel.getLimitModel().getTokenBucketMaxSize());
            preparedStatement.setObject(9, gatewayConfigModel.getLimitModel().getTokenBucketRefillRate());
            preparedStatement.setObject(10, gatewayConfigModel.getLimitModel().getLeakyBucketCapacity());
            preparedStatement.setObject(11, gatewayConfigModel.getLimitModel().getLeakyBucketRefillRate());
            preparedStatement.execute();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql insert gateway_config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    public void insert(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insert_server_sql_template)) {
            preparedStatement.setString(1, proxy);
            preparedStatement.setString(2, provider);
            preparedStatement.setInt(3, version);
            preparedStatement.setString(4, serverNodeModel.getIp());
            preparedStatement.setInt(5, serverNodeModel.getPort());
            preparedStatement.setBoolean(6, serverNodeModel.isStatus());
            preparedStatement.execute();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql insert server_config error >>>>>>>>>>>>", e);
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

    public void update(CallTrendModel callTrendModel) {
        ProxyProviderModel proxyProviderModel = callTrendModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(update_call_trend_sql_template)) {
            preparedStatement.setLong(1, callTrendModel.getCount());
            preparedStatement.setString(2, proxy);
            preparedStatement.setString(3, provider);
            preparedStatement.setInt(4, version);
            preparedStatement.setString(5, callTrendModel.getMethodName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update call trend error >>>>>>>>>>>>", e);
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
            preparedStatement.setObject(1, gatewayConfigModel.getTimeout());
            preparedStatement.setInt(2, gatewayConfigModel.getLimitModel().getLimitAlgorithm());
            preparedStatement.setObject(3, gatewayConfigModel.getLimitModel().getSlideDateWindowSize());
            preparedStatement.setObject(4, gatewayConfigModel.getLimitModel().getSlideWindowMaxRequestCount());
            preparedStatement.setObject(5, gatewayConfigModel.getLimitModel().getTokenBucketMaxSize());
            preparedStatement.setObject(6, gatewayConfigModel.getLimitModel().getTokenBucketRefillRate());
            preparedStatement.setObject(7, gatewayConfigModel.getLimitModel().getLeakyBucketCapacity());
            preparedStatement.setObject(8, gatewayConfigModel.getLimitModel().getLeakyBucketRefillRate());
            preparedStatement.setString(9, proxy);
            preparedStatement.setString(10, provider);
            preparedStatement.setInt(11, version);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update gateway config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
    }

    public void update(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(update_server_config_sql_template)) {
            preparedStatement.setBoolean(1, serverNodeModel.isStatus());
            preparedStatement.setString(2, proxy);
            preparedStatement.setString(3, provider);
            preparedStatement.setInt(4, version);
            preparedStatement.setString(5, serverNodeModel.getIp());
            preparedStatement.setInt(6, serverNodeModel.getPort());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql update server config error >>>>>>>>>>>>", e);
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
            log.error("<<<<<<<<<<<< mysql query gateway config error >>>>>>>>>>>>", e);
            throw new DaoException(e);
        }
        return list;
    }

    private List<ServerConfigPO> queryServerConfigList(int index, int size) {
        List<ServerConfigPO> list = Lists.newArrayList();
        try (DruidPooledConnection connection = druidDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from server_config limit ?, ?")) {
            preparedStatement.setInt(1, index);
            preparedStatement.setInt(2, size);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ServerConfigPO serverConfigPO = serverConfigConversion(resultSet);
                list.add(serverConfigPO);
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<<< mysql query server config error >>>>>>>>>>>>", e);
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

    private ServerConfigPO serverConfigConversion(ResultSet result) throws SQLException {
        ServerConfigPO serverConfigPO = new ServerConfigPO();
        serverConfigPO.setId(result.getLong("id"));
        serverConfigPO.setCreateTime(result.getDate("gmt_create"));
        serverConfigPO.setUpdateTime(result.getDate("gmt_modified"));
        serverConfigPO.setProxy(result.getString("proxy"));
        serverConfigPO.setProvider(result.getString("provider"));
        serverConfigPO.setVersion(result.getInt("version"));
        serverConfigPO.setIp(result.getString("ip"));
        serverConfigPO.setPort(result.getInt("port"));
        serverConfigPO.setStatus(result.getBoolean("status"));
        return serverConfigPO;
    }

    private GatewayConfigPO gatewayConfigConversion(ResultSet result) throws SQLException {
        GatewayConfigPO gatewayConfigPO = new GatewayConfigPO();
        gatewayConfigPO.setId(result.getLong("id"));
        gatewayConfigPO.setCreateTime(result.getDate("gmt_create"));
        gatewayConfigPO.setUpdateTime(result.getDate("gmt_modified"));
        gatewayConfigPO.setProxy(result.getString("proxy"));
        gatewayConfigPO.setProvider(result.getString("provider"));
        gatewayConfigPO.setVersion(result.getInt("version"));
        int limitAlgorithm = result.getInt("limit_algorithm");
        long slideDateWindowSize = result.getLong("slide_date_window_size");
        int slideWindowMaxRequestCount = result.getInt("slide_window_max_request_count");
        int tokenBucketMaxSize = result.getInt("token_bucket_max_size");
        int tokenBucketRefillRate = result.getInt("token_bucket_refill_rate");
        int leakyBucketCapacity = result.getInt("leaky_bucket_capacity");
        int leakyBucketRefillRate = result.getInt("leaky_bucket_refill_rate");
        LimitModel limitModel = new LimitModel(limitAlgorithm, slideDateWindowSize, slideWindowMaxRequestCount, tokenBucketMaxSize, tokenBucketRefillRate, leakyBucketCapacity, leakyBucketRefillRate);
        gatewayConfigPO.setLimit(limitModel);
        gatewayConfigPO.setTimeout(result.getLong("timeout"));
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
    private class GatewayConfigPO extends BasePO {
        private int version;
        private Long timeout;
        private LimitModel limit;
    }

    @Data
    private class ServerConfigPO extends BasePO {
        private String ip;
        private int port;
        private boolean status;
    }

    @Data
    private class CallTrendPO extends BasePO {
        private String methodName;
        private Long count;
    }

    @Data
    private class BasePO {
        private Long id;
        private Date createTime;
        private Date updateTime;
        private String proxy;
        private String provider;
        private int version;
    }
}
