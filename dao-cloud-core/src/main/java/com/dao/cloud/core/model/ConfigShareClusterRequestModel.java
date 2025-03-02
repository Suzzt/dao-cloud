package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/2/27 14:22
 * Synchronization of configuration information between clusters
 */
@Data
public class ConfigShareClusterRequestModel extends AbstractShareClusterRequestModel {
    /**
     * 配置类别信息
     */
    private ProxyConfigModel proxyConfigModel;

    /**
     * 配置数据内容
     */
    private String configJson;
}
