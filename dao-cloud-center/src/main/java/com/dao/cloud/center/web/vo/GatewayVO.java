package com.dao.cloud.center.web.vo;

import lombok.Data;

/**
 * @author sucf
 * @date 2024/2/6 00:15
 * @description 网关设置对象
 */
@Data
public class GatewayVO extends ServiceBaseVO {

    /**
     * 限流算法
     */
    private Integer limitAlgorithm;

    /**
     * 限流数量
     */
    private Integer limitNumber;

    /**
     * 超时时间
     */
    private Long timeout;
}
