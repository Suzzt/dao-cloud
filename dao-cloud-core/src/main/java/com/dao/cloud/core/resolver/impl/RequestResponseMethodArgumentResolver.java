package com.dao.cloud.core.resolver.impl;

import com.dao.cloud.core.resolver.MethodArgumentResolver;
import com.dao.cloud.core.model.DaoCloudServletRequest;
import com.dao.cloud.core.model.DaoCloudServletResponse;

import java.lang.reflect.Parameter;
import org.springframework.core.Ordered;

/**
 * @author wuzhenhong
 * @since 1.0.0
 * @date 2024/2/19 15:29
 */
public class RequestResponseMethodArgumentResolver implements MethodArgumentResolver, Ordered {

    @Override
    public boolean support(Parameter parameter, DaoCloudServletRequest httpServletRequest,
        DaoCloudServletResponse daoCloudServletResponse) {
        Class<?> clazz = parameter.getType();
        return clazz == DaoCloudServletRequest.class || clazz == DaoCloudServletResponse.class;
    }

    @Override
    public Object resolver(Parameter parameter, DaoCloudServletRequest httpServletRequest,
        DaoCloudServletResponse daoCloudServletResponse) {
        Class<?> clazz = parameter.getType();
        return clazz == DaoCloudServletRequest.class
            ? httpServletRequest
            : daoCloudServletResponse;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
