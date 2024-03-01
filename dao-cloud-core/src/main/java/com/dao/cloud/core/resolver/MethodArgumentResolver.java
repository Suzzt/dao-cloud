package com.dao.cloud.core.resolver;

import com.dao.cloud.core.model.DaoCloudServletRequest;
import com.dao.cloud.core.model.DaoCloudServletResponse;
import java.lang.reflect.Parameter;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:31
 */
public interface MethodArgumentResolver {

    boolean support(Parameter parameter, DaoCloudServletRequest httpServletRequest, DaoCloudServletResponse daoCloudServletResponse);

    Object resolver(Parameter parameter, DaoCloudServletRequest httpServletRequest, DaoCloudServletResponse daoCloudServletResponse);
}
