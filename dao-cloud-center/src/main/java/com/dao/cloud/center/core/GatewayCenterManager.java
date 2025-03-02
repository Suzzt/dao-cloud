package com.dao.cloud.center.core;

import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.core.model.GatewayConfigModel;
import com.dao.cloud.core.model.GatewayModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Map;


/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/2/5 13:59
 * 网关中心
 */
@Slf4j
public class GatewayCenterManager {

    /**
     * 限流设置
     */
    private Map<ProxyProviderModel, GatewayConfigModel> cache;

    /**
     * 配置信息的持久化
     */
    @Resource
    private Persistence persistence;

    /**
     * 初始化拉取配置中心的配置信息到本地服务的缓存内存中
     */
    public void init() {
        cache = persistence.loadGateway();
    }

    /**
     * Save gateway current limiting configurationO
     *
     * @param proxyProviderModel
     * @param gatewayConfigModel
     */
    public void save(ProxyProviderModel proxyProviderModel, GatewayConfigModel gatewayConfigModel) {
        cache.put(proxyProviderModel, gatewayConfigModel);
        // Storage persistence
        persistence.storage(new GatewayModel(proxyProviderModel, gatewayConfigModel));
    }

    /**
     * Clear gateway current limiting configuration
     *
     * @param proxyProviderModel
     */
    public void clear(ProxyProviderModel proxyProviderModel) {
        cache.remove(proxyProviderModel);
        persistence.delete(proxyProviderModel);
    }

    /**
     * Get current limit information
     *
     * @param proxyProviderModel
     * @return
     */
    public GatewayConfigModel getGatewayConfig(ProxyProviderModel proxyProviderModel) {
        return cache.get(proxyProviderModel);
    }

    /**
     * Get full gateway information
     *
     * @return
     */
    public Map<ProxyProviderModel, GatewayConfigModel> getGatewayConfig() {
        return cache;
    }
}
