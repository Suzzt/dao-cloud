package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/11/15 22:46
 * @description: Pull Remote Configuration Property Response Model
 */
@Data
public class ConfigurationPropertyResponseModel extends ErrorResponseModel {
    private Long sequenceId;
    private String content;
}
