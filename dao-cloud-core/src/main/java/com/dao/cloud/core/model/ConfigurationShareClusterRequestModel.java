package com.dao.cloud.core.model;

import lombok.Data;

/**
 * configuration file share cluster request model
 *
 * @author sucf
 * @since 1.0
 */
@Data
public class ConfigurationShareClusterRequestModel extends AbstractShareClusterRequestModel {
    private String proxy;
    private String groupId;
    private String fileName;
    private String content;
}
