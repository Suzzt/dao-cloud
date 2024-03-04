package com.dao.cloud.center.web.vo;

import com.dao.cloud.core.model.LimitModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author sucf
 * @date 2024/2/6 00:15
 * @description 网关设置对象
 */
@Data
public class GatewayVO {

    @NotNull(message = "proxy不能为空")
    private String proxy;

    @NotNull(message = "provider不能为空")
    private String provider;

    @NotNull(message = "version不能为空")
    private Integer version;

    /**
     * 限流信息
     */
    private LimitModel limit;

    /**
     * 网关超时时间
     */
    private Long timeout;
}
