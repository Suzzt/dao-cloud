package com.junmo.center.web.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sucf
 * @date: 2023/2/26 23:11
 * @description:
 */
@Data
public class ConfigVO extends ServiceBaseVO {
    @NotNull(message = "value不能为空")
    private String content;
}
