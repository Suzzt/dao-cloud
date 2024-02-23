package com.junmo.core.resolver.impl;

import com.junmo.core.model.HttpServletRequestModel;
import com.junmo.core.model.HttpServletResponse;
import com.junmo.core.resolver.MethodArgumentResolver;
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
