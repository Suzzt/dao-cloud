package com.dao.cloud.core.expand;

import com.dao.cloud.core.model.*;

import java.util.Map;

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
     * delete config
     *
     * @param proxyConfigModel
     */
    void delete(ProxyConfigModel proxyConfigModel);

    /**
     * delete config
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

}
