package com.junmo.core.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2024/1/8 23:53
 * @description: 在网关侧, 需要拉取所有注册的服务
 */
@Data
public class GatewayServiceNodeModel extends ErrorResponseModel {
    private Map<ProxyProviderModel, Set<ServerNodeModel>> services;
    private Map<ProxyProviderModel, GatewayConfigModel> config;
}
