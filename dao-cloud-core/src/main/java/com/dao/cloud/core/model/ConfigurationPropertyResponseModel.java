package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/11/15 22:46
 * Pull Remote Configuration Property Response Model
 */
@Data
public class ConfigurationPropertyResponseModel extends ErrorResponseModel {
    private Long sequenceId;
    private String content;
}
