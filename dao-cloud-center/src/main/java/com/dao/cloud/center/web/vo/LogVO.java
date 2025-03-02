package com.dao.cloud.center.web.vo;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/8/21 15:58
 */
@Data
public class LogVO {
    private String proxy;
    private String provider;
    private Integer version;
    private String node;
    private String log;
}
