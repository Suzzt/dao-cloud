package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/11/15 22:43
 * Pull Remote Configuration Property Request Model
 */
@Data
public class ConfigurationPropertyRequestModel extends Model {
    private Long sequenceId;
    private String proxy;
    private String groupId;
    private String fileName;
}
