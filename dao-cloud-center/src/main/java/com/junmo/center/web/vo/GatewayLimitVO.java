package com.junmo.center.web.vo;

import com.junmo.core.model.LimitModel;
import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/6 00:15
 * @description:
 */
@Data
public class GatewayLimitVO extends ServiceBaseVO {

    /**
     * 限流
     */
    private LimitModel limit;
}
