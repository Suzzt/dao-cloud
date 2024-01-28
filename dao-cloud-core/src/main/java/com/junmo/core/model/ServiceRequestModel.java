package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/1/28 22:36
 * @description:
 */
@Data
public class ServiceRequestModel extends Model {

    /**
     * 序列id
     */
    protected long sequenceId;

    /**
     * 调用接口名，在服务端找到它对应的实现
     */
    protected String provider;

    /**
     * 版本
     */
    protected int version;

    /**
     * 调用接口中方法名
     */
    protected String methodName;
}
