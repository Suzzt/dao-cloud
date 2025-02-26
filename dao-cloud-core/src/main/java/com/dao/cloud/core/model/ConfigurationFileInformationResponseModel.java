package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Set;


/**
 * @author sucf
 * @since 1.0
 * 响应: 获取center分组下所有的配置文件名称
 */
@Data
public class ConfigurationFileInformationResponseModel extends ErrorResponseModel {
    private Set<String> fileNames;
}
