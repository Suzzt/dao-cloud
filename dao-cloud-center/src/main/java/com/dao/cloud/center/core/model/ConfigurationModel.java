package com.dao.cloud.center.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ConfigurationModel {
    private String proxy;
    private String groupId;
    private String fileName;

    public ConfigurationModel(String proxy, String groupId, String fileName) {
        this.proxy = proxy;
        this.groupId = groupId;
        this.fileName = fileName;
    }
}
