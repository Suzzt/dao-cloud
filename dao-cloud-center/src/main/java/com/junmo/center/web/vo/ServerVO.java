package com.junmo.center.web.vo;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/7 20:43
 * @description:
 */
@Data
public class ServerVO {
    private String proxy;

    private String provider;

    private Integer version;

    private Integer number;
}
