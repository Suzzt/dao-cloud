package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/12 21:01
 * @description:
 */
@Data
public class GatewayConfigModel extends Model {

    private LimitModel limitModel;

    private Long timeout;
}
