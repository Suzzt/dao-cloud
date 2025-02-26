package com.dao.cloud.center.web.vo;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class LogVO {
    private String proxy;
    private String provider;
    private Integer version;
    private String node;
    private String log;
}
