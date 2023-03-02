package com.junmo.center.core.storage;

import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;

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
     * delete config
     * @param proxyConfigModel
     */
    void delete(ProxyConfigModel proxyConfigModel);

    /**
     * get json value
     * @param proxyConfigModel
     * @return
     */
    String getValue(ProxyConfigModel proxyConfigModel);

    /**
     * load init all config data
     * @return
     */
    Map<ProxyConfigModel, String> load();
}
