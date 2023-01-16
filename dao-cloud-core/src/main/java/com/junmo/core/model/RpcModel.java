package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/28 21:56
 * @description:
 */
@Data
public class RpcModel extends Model {
    /**
     * 消息序号
     */
    private long sequenceId;
}
