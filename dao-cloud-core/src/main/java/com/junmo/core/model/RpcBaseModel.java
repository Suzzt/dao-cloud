package com.junmo.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: sucf
 * @date: 2022/10/28 21:56
 * @description:
 */
@Data
public abstract class RpcBaseModel implements Serializable {
    /**
     * 消息序号
     */
    private long sequenceId;
}
