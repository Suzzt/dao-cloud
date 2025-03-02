package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Set;


/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/11/11 22:35
 * 响应: 获取center分组下所有的配置文件名称
 */
@Data
public class ConfigurationFileInformationResponseModel extends ErrorResponseModel {
    private Long sequenceId;
    private Set<String> fileNames;
}
