package com.dao.cloud.center.web.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sucf
 * @date: 2024/11/24 20:39
 * @description:
 */
@Data
public class ConfigurationPropertyVO {
    @NotNull(message = "property不能为空")
    private String property;
}
