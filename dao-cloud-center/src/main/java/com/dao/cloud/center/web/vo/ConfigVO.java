package com.dao.cloud.center.web.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sucf
 * @date: 2023/2/26 23:11
 * @description:
 */
@Data
public class ConfigVO {

    @NotNull(message = "proxy不能为空")
    private String proxy;

    @NotNull(message = "key不能为空")
    private String key;

    @NotNull(message = "version不能为空")
    private Integer version;

    @NotNull(message = "content不能为空")
    private String content;
}
