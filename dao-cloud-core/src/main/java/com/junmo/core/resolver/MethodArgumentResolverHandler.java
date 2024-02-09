package com.junmo.core.resolver;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.HttpServletRequestModel;
import com.junmo.core.resolver.impl.FormDataMethodArgumentResolver;
import com.junmo.core.resolver.impl.JsonMethodArgumentResolver;
import com.junmo.core.resolver.impl.PrimitiveMethodArgumentResolver;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.convert.ConversionService;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:53
 */
public class MethodArgumentResolverHandler {

    public static final MethodArgumentResolverHandler DEFAULT_RESOLVER = new MethodArgumentResolverHandler();

    private ConversionService conversionService;
    private List<MethodArgumentResolver> resolverList;

    public MethodArgumentResolverHandler() {
        this.resolverList = new ArrayList<>();
        this.addDefaultResolvers();
    }

    public MethodArgumentResolverHandler(ConversionService conversionService) {
        this();
        this.conversionService = conversionService;
    }

    public Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest) {
        for (MethodArgumentResolver resolver : resolverList) {
            if (resolver.support(parameter, httpServletRequest)) {
                return resolver.resolver(parameter, httpServletRequest);
            }
        }
        throw new DaoException(
            String.format("不能解析类型为【%s】，名字为【%s】的请求参数！", parameter.getType().getName(),
                parameter.getName()));
    }

    private void addDefaultResolvers() {
        this.resolverList.add(new PrimitiveMethodArgumentResolver(this.conversionService));
        this.resolverList.add(new JsonMethodArgumentResolver());
        this.resolverList.add(new FormDataMethodArgumentResolver(this.conversionService));
    }

    /**
     * 添加自定义解析器
     */
    public void addCustomerResolver(MethodArgumentResolver argumentResolver) {
        this.resolverList.add(argumentResolver);
    }
}