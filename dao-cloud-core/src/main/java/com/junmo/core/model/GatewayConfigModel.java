package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/12 21:01
 * @description:
 */
@Data
public class GatewayConfigModel {

    private LimitModel limitModel;

    private Long timeout;
}
