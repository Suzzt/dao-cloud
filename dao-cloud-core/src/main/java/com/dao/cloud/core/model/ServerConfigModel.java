package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Map;

/**
 * @author: sucf
 * @date: 2024/4/1 11:08
 * @description: server config information model
 */
@Data
public class ServerConfigModel extends ErrorResponseModel {
    private Map<ProxyProviderModel, ServerNodeModel> serverConfig;
}
