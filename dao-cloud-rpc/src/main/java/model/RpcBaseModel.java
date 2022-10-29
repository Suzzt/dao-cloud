package model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/28 21:56
 * @description:
 */
@Data
public abstract class RpcBaseModel {
    /**
     * 消息序号
     */
    private long sequenceId;
}
