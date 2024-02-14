package com.junmo.center.core;

import com.junmo.center.web.vo.GatewayVO;
import com.junmo.center.web.vo.ServiceBaseVO;
import com.junmo.core.expand.Persistence;
import com.junmo.core.model.GatewayConfigModel;
import com.junmo.core.model.GatewayModel;
import com.junmo.core.model.LimitModel;
import com.junmo.core.model.ProxyProviderModel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Map;


/**
 * @author: sucf
 * @date: 2024/2/5 13:59
 * @description: 网关中心
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
     * Save gateway current limiting configuration
     *
     * @param gatewayVO
     */
    public void save(GatewayVO gatewayVO) {
        String proxy = gatewayVO.getProxy();
        String key = gatewayVO.getKey();
        Integer version = gatewayVO.getVersion();
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, key, version);
        GatewayConfigModel gatewayConfigModel = new GatewayConfigModel();
        LimitModel limitModel = new LimitModel(gatewayVO.getLimitAlgorithm(), gatewayVO.getLimitNumber());
        gatewayConfigModel.setLimitModel(limitModel);
        gatewayConfigModel.setTimeout(gatewayVO.getTimeout());
        cache.put(proxyProviderModel, gatewayConfigModel);
        // Storage persistence
        persistence.storage(new GatewayModel(proxyProviderModel, gatewayConfigModel));
        // TODO: sync other cluster node
    }

    /**
     * Clear gateway current limiting configuration
     *
     * @param serviceBaseVO
     */
    public void clear(ServiceBaseVO serviceBaseVO) {
        String proxy = serviceBaseVO.getProxy();
        String key = serviceBaseVO.getKey();
        Integer version = serviceBaseVO.getVersion();
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, key, version);
        cache.remove(proxyProviderModel);
        persistence.delete(proxyProviderModel);
        // TODO: sync other cluster node
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
