package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/1/8 23:53
 * 在网关侧, 需要拉取所有注册的服务
 */
@Data
public class GatewayServiceNodeModel extends ErrorResponseModel {
    private Map<ProxyProviderModel, Set<ServerNodeModel>> services;
    private Map<ProxyProviderModel, GatewayConfigModel> config;
}
