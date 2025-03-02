package com.dao.cloud.core.resolver.impl;

import com.dao.cloud.core.model.DaoCloudServletRequest;
import com.dao.cloud.core.model.DaoCloudServletResponse;
import com.dao.cloud.core.resolver.AbstractMethodArgumentResolver;
import com.dao.cloud.core.util.HttpGenericInvokeUtils;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ClassUtils;

/**
 * @author wuzhenhong
 * @since 1.0.0
 * @date 2024/2/8 14:34
 */
public class PrimitiveMethodArgumentResolver extends AbstractMethodArgumentResolver {

    public PrimitiveMethodArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public boolean support(Parameter parameter, DaoCloudServletRequest httpServletRequest, DaoCloudServletResponse daoCloudServletResponse) {
        Class<?> type = parameter.getType();
        return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class;
    }

    @Override
    public Object resolver(Parameter parameter, DaoCloudServletRequest httpServletRequest, DaoCloudServletResponse daoCloudServletResponse) {
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
