package com.junmo.core.resolver;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.HttpServletRequestModel;
import com.junmo.core.model.HttpServletResponse;
import com.junmo.core.resolver.impl.FormDataMethodArgumentResolver;
import com.junmo.core.resolver.impl.JsonMethodArgumentResolver;
import com.junmo.core.resolver.impl.PrimitiveMethodArgumentResolver;
import com.junmo.core.resolver.impl.RequestResponseMethodArgumentResolver;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:53
 */
public class MethodArgumentResolverHandler {

    public static final MethodArgumentResolverHandler DEFAULT_RESOLVER = new MethodArgumentResolverHandler();

    private ConversionService conversionService;
    private List<MethodArgumentResolver> resolverList;

    public MethodArgumentResolverHandler() {
        this.init();
    }

    public MethodArgumentResolverHandler(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.init();
    }

    private void init() {
        this.resolverList = new ArrayList<>();
        this.addDefaultResolvers();
    }

    public Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest,
        HttpServletResponse httpServletResponse) {
        for (MethodArgumentResolver resolver : resolverList) {
            if (resolver.support(parameter, httpServletRequest, httpServletResponse)) {
                return resolver.resolver(parameter, httpServletRequest, httpServletResponse);
            }
        }
        throw new DaoException(
            String.format("不能解析类型为【%s】，名字为【%s】的请求参数！", parameter.getType().getName(),
                parameter.getName()));
    }

    private void addDefaultResolvers() {
        this.resolverList.add(new RequestResponseMethodArgumentResolver());
        this.resolverList.add(new PrimitiveMethodArgumentResolver(this.conversionService));
        this.resolverList.add(new JsonMethodArgumentResolver());
        this.resolverList.add(new FormDataMethodArgumentResolver(this.conversionService));
    }

    /**
     * 添加自定义解析器
     */
    public void addCustomerResolvers(List<MethodArgumentResolver> argumentResolverList) {
        if(CollectionUtils.isEmpty(argumentResolverList)) {
            return;
        }
        this.resolverList.addAll(argumentResolverList);
        // 排序
        AnnotationAwareOrderComparator.sort(this.resolverList);
    }
}
