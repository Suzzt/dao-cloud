package com.dao.cloud.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sucf
 * @date 2025/5/3 21:19
 * @since 1.0.0
 */
@Getter
@Setter
public class AckServiceLoadResponseModel extends MessageModel {
    /**
     * 服务负载连接数
     */
    private int number;
}
