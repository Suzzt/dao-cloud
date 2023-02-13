package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/28 21:50
 * @description: rpc 请求模型封装
 */
@Data
public class RpcRequestModel extends ResponseModel {
    private long sequenceId;

    /**
     * 调用接口名，在服务端找到它对应的实现
     */
    private String provider;
    /**
     * 版本
     */
    private int version;
    /**
     * 调用接口中方法名
     */
    private String methodName;
    /**
     * 方法返回类型
     */
    private Class<?> returnType;
    /**
     * 方法参数类型数组
     */
    private Class[] parameterTypes;
    /**
     * 方法参数值数组
     */
    private Object[] parameterValue;

    public RpcRequestModel(long sequenceId, String provider, int version, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        this.sequenceId = sequenceId;
        this.provider = provider;
        this.version = version;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }
}
