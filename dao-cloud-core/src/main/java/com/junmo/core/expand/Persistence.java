package com.junmo.core.expand;

import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.GatewayModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.model.ProxyProviderModel;

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
    Map<ProxyConfigModel, String> load();

}
