package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ConfigurationFileResponseModel extends ErrorResponseModel {
    private Set<ConfigurationFileInformationModel> files;
}
