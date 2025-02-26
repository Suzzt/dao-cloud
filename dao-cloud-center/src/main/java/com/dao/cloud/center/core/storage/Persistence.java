package com.dao.cloud.center.core.storage;

import com.dao.cloud.center.core.model.ConfigurationModel;
import com.dao.cloud.center.core.model.ConfigurationProperty;
import com.dao.cloud.center.core.model.ServerProxyProviderNode;
import com.dao.cloud.center.web.vo.CallTrendVO;
import com.dao.cloud.core.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/2/28 23:41
 * @description: dao center data persistence.
 * if you want to implement persistence according to your own rules, implement this method
 */
public interface Persistence {

    /**
     * storage config
     *
     * @param configModel
     */
    void storage(ConfigModel configModel);

    /**
     * storage gateway
     *
     * @param gatewayModel
     */
    void storage(GatewayModel gatewayModel);

    /**
     * storage server
     * (This is an idempotent operation)
     *
     * @param proxyProviderModel
     * @param serverNodeModel
     */
    void storage(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel);

    /**
     * storage configuration
     *
     * @param configurationProperty
     */
    void storage(ConfigurationProperty configurationProperty);

    /**
     * delete config
     *
     * @param proxyConfigModel
     */
    void delete(ProxyConfigModel proxyConfigModel);

    /**
     * delete configuration
     *
     * @param configurationProperty
     */
    void delete(ConfigurationProperty configurationProperty);

    /**
     * delete server
     *
     * @param proxyProviderModel
     */
    void delete(ProxyProviderModel proxyProviderModel);

    /**
     * load init all config data
     *
     * @return
     */
    Map<ProxyConfigModel, String> loadConfig();

    /**
     * load init all gateway data
     *
     * @return
     */
    Map<ProxyProviderModel, GatewayConfigModel> loadGateway();

    /**
     * load init all server status
     *
     * @return
     */
    Map<ServerProxyProviderNode, Boolean> loadServer();

    /**
     * 获取所有配置文件信息
     *
     * @return ConfigurationModel
     */
    List<ConfigurationModel> getConfiguration();

    /**
     * 获取配置文件内容
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param fileName the file name
     * @return the file content
     */
    String getConfigurationProperty(String proxy, String groupId, String fileName);

    /**
     * clear
     */
    void clear();

    /**
     * method call increment
     *
     * @param callTrendModel
     */
    void callTrendIncrement(CallTrendModel callTrendModel);

    /**
     * get call trend count
     *
     * @param proxyProviderModel
     * @return
     */
    List<CallTrendVO> getCallCount(ProxyProviderModel proxyProviderModel);

    /**
     * clear call record
     *
     * @param proxyProviderModel
     * @param methodName
     */
    void callTrendClear(ProxyProviderModel proxyProviderModel, String methodName);

    /**
     * get all call trend data
     *
     * @return
     */
    List<CallTrendModel> getCallTrends();
}
