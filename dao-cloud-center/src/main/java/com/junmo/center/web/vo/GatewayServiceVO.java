package com.junmo.center.web.vo;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/1/19 10:17
 * @description:
 */
@Data
public class GatewayServiceVO {
    private String proxy;

    private String provider;

    private Integer version;

    private String method;
}
