package com.dao.cloud.core.resolver;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.DaoCloudServletRequest;
import com.dao.cloud.core.model.DaoCloudServletResponse;
import com.dao.cloud.core.resolver.impl.FormDataMethodArgumentResolver;
import com.dao.cloud.core.resolver.impl.JsonMethodArgumentResolver;
import com.dao.cloud.core.resolver.impl.PrimitiveMethodArgumentResolver;
import com.dao.cloud.core.resolver.impl.RequestResponseMethodArgumentResolver;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;

/**
 * @author wuzhenhong
 * @since 1.0.0
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

    public Object resolver(Parameter parameter, DaoCloudServletRequest httpServletRequest,
        DaoCloudServletResponse daoCloudServletResponse) {
        for (MethodArgumentResolver resolver : resolverList) {
            if (resolver.support(parameter, httpServletRequest, daoCloudServletResponse)) {
                return resolver.resolver(parameter, httpServletRequest, daoCloudServletResponse);
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
