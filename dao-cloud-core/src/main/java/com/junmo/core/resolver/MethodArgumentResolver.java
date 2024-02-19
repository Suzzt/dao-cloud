package com.junmo.core.resolver;

import com.junmo.core.model.HttpServletRequestModel;
import com.junmo.core.model.HttpServletResponse;
import java.lang.reflect.Parameter;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:31
 */
public interface MethodArgumentResolver {

    boolean support(Parameter parameter, HttpServletRequestModel httpServletRequest, HttpServletResponse httpServletResponse);

    Object resolver(Parameter parameter, HttpServletRequestModel httpServletRequest, HttpServletResponse httpServletResponse);
}
