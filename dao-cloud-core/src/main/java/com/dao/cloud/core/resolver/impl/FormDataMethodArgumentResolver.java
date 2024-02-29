package com.dao.cloud.core.resolver.impl;

import com.dao.cloud.core.binder.WebDataBinder;
import com.dao.cloud.core.model.HttpServletRequestModel;
import com.dao.cloud.core.model.HttpServletResponse;
import com.dao.cloud.core.resolver.AbstractMethodArgumentResolver;
import com.dao.cloud.core.util.HttpGenericInvokeUtils;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ClassUtils;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:34
 */
public class FormDataMethodArgumentResolver extends AbstractMethodArgumentResolver {

    public FormDataMethodArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public boolean support(Parameter parameter, HttpServletRequestModel httpServletRequest, HttpServletResponse httpServletResponse) {
        String contentType = Optional.ofNullable(httpServletRequest.getHeads().get("content-type")).orElse("");
        Class<?> type = parameter.getType();
        contentType = Objects.isNull(contentType) ? "" : contentType;
        String[] headerArr = HttpGenericInvokeUtils.splitHeaderContentType(contentType);
        return !ClassUtils.isPrimitiveOrWrapper(type)
            && type != String.class
            && (Objects.isNull(headerArr) || headerArr.length == 0 || !headerArr[0].equals(
            HttpHeaderValues.APPLICATION_JSON.toString()));
    }

    @Override
    public Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest, HttpServletResponse httpServletResponse) {

        Constructor<?> ctor = BeanUtils.getResolvableConstructor(parameter.getType());
        Object target = BeanUtils.instantiateClass(ctor);
        MutablePropertyValues mpvs = new MutablePropertyValues();
        Map<String, String[]> getParam = Optional.ofNullable(httpServletRequest.getParams())
            .orElse(Collections.emptyMap());
        getParam.forEach((k, v) -> {
            Arrays.stream(v).forEach(value -> {
                mpvs.add(k, value);
            });
        });
        WebDataBinder webDataBinder = new WebDataBinder(target, conversionService);
        webDataBinder.bind(mpvs);
        return target;
    }
}
