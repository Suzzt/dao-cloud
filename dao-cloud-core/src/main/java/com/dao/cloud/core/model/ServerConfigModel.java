package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Map;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/4/1 11:08
 * server config information model
 */
@Data
public class ServerConfigModel extends ErrorResponseModel {
    private Map<ProxyProviderModel, ServerNodeModel> serverConfig;
}
