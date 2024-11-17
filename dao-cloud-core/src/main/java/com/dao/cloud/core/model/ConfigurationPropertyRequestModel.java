package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/11/15 22:43
 * @description: Pull Remote Configuration Property Request Model
 */
@Data
public class ConfigurationPropertyRequestModel extends Model {
    private String groupId;
    private int version;
    private String fileName;
}