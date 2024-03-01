package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/28 21:50
 * @description: rpc 请求模型封装
 */
@Data
public class RpcRequestModel extends ServiceRequestModel {

    private boolean http;

    private HttpServletResponse httpServletResponse;

    /**
     * 方法参数类型数组
     */
    private Class[] parameterTypes;

    /**
     * 方法参数值数组
     */
    private Object[] parameterValue;

    /**
     * 方法返回类型
     */
    protected Class<?> returnType;

    public RpcRequestModel(String provider, int version, String methodName, Class[] parameterTypes, Object[] parameterValue, Class<?> returnType) {
        this.provider = provider;
        this.version = version;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }
}
