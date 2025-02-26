package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Map;

/**
 * @author sucf
 * @since 1.0
 * server config information model
 */
@Data
public class ServerConfigModel extends ErrorResponseModel {
    private Map<ProxyProviderModel, ServerNodeModel> serverConfig;
}
