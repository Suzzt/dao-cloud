package com.junmo.gateway;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.model.GatewayRequestModel;
import com.junmo.core.model.GatewayResponseModel;
import com.junmo.gateway.auth.Interceptor;
import com.junmo.gateway.limit.Limiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        // 获取到入参参数信息（todo）
        GatewayRequestModel gatewayRequestModel = new GatewayRequestModel();
        return doService(gatewayRequestModel);
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
        // 获取到入参参数信息（todo）
        GatewayRequestModel gatewayRequestModel = new GatewayRequestModel();
        return doService(gatewayRequestModel);
    }

    public <T> T doService(GatewayRequestModel gatewayRequestModel) {
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
        GatewayResponseModel gatewayResponseModel = invoke(gatewayRequestModel);
        if (!StringUtils.hasLength(gatewayResponseModel.getErrorMessage())) {
            log.error("Gateway request failed", gatewayResponseModel.getErrorMessage());
            return (T) ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_ERROR);
        }
        return (T) gatewayResponseModel.getReturnValue();
    }

    public GatewayResponseModel invoke(GatewayRequestModel gatewayRequestModel) {
        long sequenceId = IdUtil.getSnowflake(1, 1).nextId();
        gatewayRequestModel.setSequenceId(sequenceId);
        return null;
    }
}
