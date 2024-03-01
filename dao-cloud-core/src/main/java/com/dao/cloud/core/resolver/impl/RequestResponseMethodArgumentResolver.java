package com.dao.cloud.core.resolver.impl;

import com.dao.cloud.core.resolver.MethodArgumentResolver;
import com.dao.cloud.core.model.HttpServletRequestModel;
import com.dao.cloud.core.model.HttpServletResponse;

import java.lang.reflect.Parameter;
import org.springframework.core.Ordered;

/**
 * @author wuzhenhong
 * @date 2024/2/19 15:29
 */
public class RequestResponseMethodArgumentResolver implements MethodArgumentResolver, Ordered {

    @Override
    public boolean support(Parameter parameter, HttpServletRequestModel httpServletRequest,
        HttpServletResponse httpServletResponse) {
        Class<?> clazz = parameter.getType();
        return clazz == HttpServletRequestModel.class || clazz == HttpServletResponse.class;
    }

    @Override
    public Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest,
        HttpServletResponse httpServletResponse) {
        Class<?> clazz = parameter.getType();
        return clazz == HttpServletRequestModel.class
            ? httpServletRequest
            : httpServletResponse;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
