package com.dao.cloud.core.model;

import lombok.Data;

/**
 * http 参数绑定结果
 * @author wuzhenhong
 * @date 2024/2/7 14:30
 */
@Data
public class HttpParameterBinderResult {

    /**
     * 方法参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法参数值
     */
    private Object[] parameterValues;

    private Class<?> returnType;

    private DaoCloudServletResponse daoCloudServletResponse;
}
