package com.dao.cloud.starter.context;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/1/25 23:40
 * @description: 网关http上下文带来的数据
 */
@Data
public class GatewayHttpData {
    private String param;

    private String header;
}
