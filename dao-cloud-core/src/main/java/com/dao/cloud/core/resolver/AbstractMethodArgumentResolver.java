package com.dao.cloud.core.resolver;

import com.dao.cloud.core.binder.WebDataBinder;
import java.util.Objects;
import org.springframework.core.convert.ConversionService;

/**
 * @author wuzhenhong
 * @since 1.0.0
 * @date 2024/2/8 14:31
 */
public abstract class AbstractMethodArgumentResolver implements MethodArgumentResolver {

    protected ConversionService conversionService;

    public AbstractMethodArgumentResolver(ConversionService conversionService) {
        if(Objects.isNull(conversionService)) {
            this.conversionService = WebDataBinder.CONVERSION_SERVICE;
        } else {
            this.conversionService = conversionService;
        }
    }
}
