package com.junmo.core.resolver;

import com.junmo.core.binder.WebDataBinder;
import java.util.Objects;
import org.springframework.core.convert.ConversionService;

/**
 * @author wuzhenhong
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
