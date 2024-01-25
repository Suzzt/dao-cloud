package com.junmo.gateway;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.gateway.auth.Interceptor;
import com.junmo.gateway.limit.Limiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author: sucf
 * @date: 2023/12/28 14:55
 * @description: 转发分发处理器
 */
@RestController
@Slf4j
public class Dispatcher {

    private Limiter limiter;

    public Dispatcher(Limiter limiter) {
        this.limiter = limiter;
    }

    /**
     * 网关请求主入口(get)
     *
     * @param proxy
     * @param provider
     * @param version
     * @param request
     * @param response
     */
    @RequestMapping(value = "api/{proxy}/{provider}/{version}/{method}", method = RequestMethod.GET)
    public <T> T goGet(@PathVariable String proxy, @PathVariable String provider, @PathVariable String version,
                       @PathVariable String method, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(version) || !StringUtils.hasLength(method)) {
            return null;
        }
        // 获取到入参参数信息
        return doService();
    }

    /**
     * 网关请求主入口(post)
     *
     * @param proxy
     * @param provider
     * @param version
     * @param request
     * @param response
     */
    @RequestMapping(value = "api/{proxy}/{provider}/{version}/{method}", method = RequestMethod.POST)
    public <T> T goPost(@PathVariable String proxy, @PathVariable String provider, @PathVariable String version,
                        @PathVariable String method, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(version) || !StringUtils.hasLength(method)) {
            return null;
        }
        return doService();
    }

    public <T> T doService() {
        // 先判断限流
        if (!limiter.allow()) {
            return (T) ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_LIMIT);
        }

        // 处理拦截器的责任链请求
        List<Interceptor> interceptors = Lists.newArrayList();
        for (Interceptor interceptor : interceptors) {
            if (!interceptor.action()) {
                return null;
            }
        }

        // 发起转发路由请求
        long sequenceId = IdUtil.getSnowflake(1, 1).nextId();
        String provider = null;
        int version = 0;
        String methodName = null;
        Class<?> returnType = null;
        Class[] parameterTypes = null;
        Object[] parameterValue = null;
        RpcRequestModel rpcRequestModel = new RpcRequestModel(sequenceId, provider, version, methodName, returnType, parameterTypes, parameterValue);
        RpcResponseModel rpcResponseModel = invoke(rpcRequestModel);
        if (!StringUtils.hasLength(rpcResponseModel.getErrorMessage())) {
            log.error("Gateway request failed", rpcResponseModel.getErrorMessage());
            return (T) ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_ERROR);
        }
        return (T) rpcResponseModel.getReturnValue();
    }

    public RpcResponseModel invoke(RpcRequestModel rpcRequestModel) {
        return null;
    }
}
