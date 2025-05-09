package com.dao.cloud.center.web.vo;

import com.dao.cloud.core.model.GatewayConfigModel;
import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/7 20:43
 */
@Data
public class ServerVO {
    private String proxy;

    private String provider;

    private Integer version;

    private Integer number;

    private GatewayConfigModel gateway;
}
