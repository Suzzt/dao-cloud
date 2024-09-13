package com.dao.cloud.center.web.vo;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/8/21 15:58
 * @description:
 */
@Data
public class LogVO {
    private String proxy;
    private String provider;
    private Integer version;
    private String node;
    private String log;
}
