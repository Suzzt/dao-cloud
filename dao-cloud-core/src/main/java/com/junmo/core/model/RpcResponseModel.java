package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/28 21:51
 * @description: rpc 返回模型封装
 */
@Data
public class RpcResponseModel extends RpcBaseModel {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;
}
