package com.junmo.core.resolver;

import com.junmo.core.model.HttpServletRequestModel;
import java.lang.reflect.Parameter;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:31
 */
public interface MethodArgumentResolver {

    boolean support(Parameter parameter, HttpServletRequestModel httpServletRequest);

    Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest);
}
