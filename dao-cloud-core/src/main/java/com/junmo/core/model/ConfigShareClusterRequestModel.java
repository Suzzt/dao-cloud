package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/27 14:22
 * @description: Synchronization of configuration information between clusters
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
