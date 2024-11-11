package com.dao.cloud.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author: sucf
 * @date: 2024/11/11 22:35
 * @description: 从center上获取的配置信息
 */
@Data
public class RemoteCenterPropertyModel extends ErrorResponseModel{
    private List<String> properties;
}
