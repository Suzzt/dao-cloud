package com.dao.cloud.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author: sucf
 * @date: 2023/7/4 15:55
 * @description:
 */
@Data
public class FullConfigModel extends ErrorResponseModel {
    private List<ConfigModel> configModels;
}
