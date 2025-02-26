package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/12 14:16
 * @description: 配置中心配置对象
 */
@Data
public class ConfigModel extends ErrorResponseModel {
    /**
     * 配置类别
     * 用于区分定位唯一
     */
    private ProxyConfigModel proxyConfigModel;

    /**
     * 配置信息
     */
    private String configValue;
}
