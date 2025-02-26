package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * Pull Remote Configuration Property Response Model
 */
@Data
public class ConfigurationPropertyResponseModel extends ErrorResponseModel {
    private String content;
}
