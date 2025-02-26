package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class GatewayConfigModel extends Model {

    private LimitModel limitModel;

    private Long timeout;
}
