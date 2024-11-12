package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/11/12 22:36
 * @description: 请求: 获取center分组下所有的配置文件名称
 */
@Data
public class ConfigurationFileInformationRequestModel extends Model {
    private String proxy;
    private String groupId;
}
