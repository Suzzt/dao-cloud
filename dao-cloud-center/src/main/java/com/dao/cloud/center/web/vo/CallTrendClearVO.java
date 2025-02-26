package com.dao.cloud.center.web.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sucf
 * @date: 2024/8/10 00:26
 * @description: 清空接口调用趋势VO
 */
@Data
public class CallTrendClearVO {
    @NotNull(message = "proxy不能为空")
    private String proxy;

    @NotNull(message = "provider不能为空")
    private String provider;

    @NotNull(message = "version不能为空")
    private Integer version;

    private String methodName;
}
