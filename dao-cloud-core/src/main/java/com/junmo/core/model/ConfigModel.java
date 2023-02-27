package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/12 14:16
 * @description:
 */
@Data
public class ConfigModel extends ResponseModel {
    private ProxyConfigModel proxyConfigModel;

    private String configValue;
}
