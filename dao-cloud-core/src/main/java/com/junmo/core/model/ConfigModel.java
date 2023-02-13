package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/12 14:16
 * @description:
 */
@Data
public class ConfigModel extends Model{
    private ProxyProviderModel proxyProviderModel;

    private String key;

    private String configValue;
}
