package com.junmo.center.core;

import com.junmo.center.web.vo.GatewayLimitVO;
import com.junmo.center.web.vo.ServiceBaseVO;
import com.junmo.core.expand.Persistence;
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
    private Map<ProxyProviderModel, LimitModel> cache;

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
     * @param gatewayLimitVO
     */
    public void save(GatewayLimitVO gatewayLimitVO) {
        String proxy = gatewayLimitVO.getProxy();
        String key = gatewayLimitVO.getKey();
        Integer version = gatewayLimitVO.getVersion();
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, key, version);
        LimitModel limitModel = new LimitModel(gatewayLimitVO.getLimitAlgorithm(), gatewayLimitVO.getLimitNumber());
        cache.put(proxyProviderModel, limitModel);
        // Storage persistence
        persistence.storage(new GatewayModel(proxyProviderModel, limitModel));
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
    public LimitModel getLimiter(ProxyProviderModel proxyProviderModel) {
        return cache.get(proxyProviderModel);
    }
}
