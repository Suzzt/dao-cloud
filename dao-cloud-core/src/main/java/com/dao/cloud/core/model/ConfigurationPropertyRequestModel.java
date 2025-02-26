package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * Pull Remote Configuration Property Request Model
 */
@Data
public class ConfigurationPropertyRequestModel extends Model {
    private Long sequenceId;
    private String proxy;
    private String groupId;
    private String fileName;
}
