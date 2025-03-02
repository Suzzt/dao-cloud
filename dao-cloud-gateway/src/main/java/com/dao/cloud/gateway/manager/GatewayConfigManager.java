package com.dao.cloud.gateway.manager;

import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.gateway.model.GatewayConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/2/14 22:13
 */
@Slf4j
public class GatewayConfigManager {

    /**
     * 网关配置
     */
    private static Map<ProxyProviderModel, GatewayConfig> config = new ConcurrentHashMap<>();

    public static GatewayConfig getGatewayConfig(ProxyProviderModel proxyProviderModel) {
        return config.get(proxyProviderModel);
    }

    public static void save(ProxyProviderModel proxyProviderModel, GatewayConfig gatewayConfig) {
        GatewayConfig oldGatewayConfig = config.get(proxyProviderModel);
        if (gatewayConfig == null) {
            // remove
            config.remove(proxyProviderModel);
            return;
        }

        if (oldGatewayConfig == null) {
            // add initialization
            config.put(proxyProviderModel, gatewayConfig);
            return;
        }

        if (!Objects.equals(gatewayConfig.getLimiter(), oldGatewayConfig.getLimiter())) {
            log.info("service = {} update gateway config. The current limiter={} has changed.", proxyProviderModel, gatewayConfig.getLimiter());
            oldGatewayConfig.setLimiter(gatewayConfig.getLimiter());
        }

        if (!Objects.equals(gatewayConfig.getTimeout(), oldGatewayConfig.getTimeout())) {
            log.info("service = {} update gateway config. The current timeout={} has changed.", proxyProviderModel, gatewayConfig.getTimeout());
            oldGatewayConfig.setTimeout(gatewayConfig.getTimeout());
        }

        if (!Objects.equals(gatewayConfig.getInterceptors(), oldGatewayConfig.getInterceptors())) {
            log.info("service = {} update gateway config. The current interceptors={} has changed.", proxyProviderModel, gatewayConfig.getInterceptors());
            oldGatewayConfig.setInterceptors(gatewayConfig.getInterceptors());
        }
    }
}
