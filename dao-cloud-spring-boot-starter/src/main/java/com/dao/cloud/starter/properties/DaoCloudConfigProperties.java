package com.dao.cloud.starter.properties;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/11/27 23:15
 * @description: 配置加载
 */
@Data
public class DaoCloudConfigProperties {
    private boolean overrideSystemProperties = true;
    private boolean allowOverride = true;
    private boolean overrideNone = false;
}
