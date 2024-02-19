package com.junmo.core.resolver.impl;

import com.junmo.core.model.HttpServletRequestModel;
import com.junmo.core.model.HttpServletResponse;
import com.junmo.core.resolver.AbstractMethodArgumentResolver;
import com.junmo.core.util.HttpGenericInvokeUtils;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ClassUtils;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:34
 */
public class PrimitiveMethodArgumentResolver extends AbstractMethodArgumentResolver {

    public PrimitiveMethodArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public boolean support(Parameter parameter, HttpServletRequestModel httpServletRequest, HttpServletResponse httpServletResponse) {
        Class<?> type = parameter.getType();
        return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class;
    }

    @Override
    public Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest, HttpServletResponse httpServletResponse) {
        String name = parameter.getName();
        Class<?> type = parameter.getType();
        Map<String, String[]> getParam = Optional.ofNullable(httpServletRequest.getParams())
            .orElse(Collections.emptyMap());
        String[] values = getParam.get(name);
        if(Objects.isNull(values) || values.length == 0) {
            return HttpGenericInvokeUtils.getInitPrimitiveValue(type);
        } else {
            String v = values[0];
            return conversionService.convert(v, type);
        }
    }
}
