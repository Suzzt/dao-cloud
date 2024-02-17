package com.junmo.gateway;

import com.google.common.collect.Lists;
import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.unit.ClientInvoker;
import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.enums.Serializer;
import com.junmo.core.model.GatewayConfigModel;
import com.junmo.core.model.GatewayRequestModel;
import com.junmo.core.model.HttpServletRequestModel;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.util.HttpGenericInvokeUtils;
import com.junmo.gateway.auth.Interceptor;
import com.junmo.gateway.global.GatewayServiceConfig;
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

    private DaoLoadBalance daoLoadBalance;

    public Dispatcher(Limiter limiter, DaoLoadBalance daoLoadBalance) {
        this.limiter = limiter;
        this.daoLoadBalance = daoLoadBalance;
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
    public ApiResult goGet(@PathVariable String proxy, @PathVariable String provider, @PathVariable() String version,
                           @PathVariable String method, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(method)) {
            return ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_PARAM_DELETION);
        }
        HttpServletRequestModel requestModel = HttpGenericInvokeUtils.buildRequest(request);
        GatewayRequestModel gatewayRequestModel = new GatewayRequestModel(provider, Byte.valueOf(version), method, requestModel);
        return doService(proxy, gatewayRequestModel);
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
    public ApiResult goPost(@PathVariable String proxy, @PathVariable String provider, @PathVariable() String version,
                            @PathVariable String method, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(version) || !StringUtils.hasLength(method)) {
            return ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_PARAM_DELETION);
        }
        HttpServletRequestModel requestModel = HttpGenericInvokeUtils.buildRequest(request);
        GatewayRequestModel gatewayRequestModel = new GatewayRequestModel(provider, Byte.valueOf(version), method, requestModel);
        return doService(proxy, gatewayRequestModel);
    }

    public ApiResult doService(String proxy, GatewayRequestModel gatewayRequestModel) {
        // 先判断限流
        if (!limiter.allow()) {
            return ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_LIMIT);
        }

        // 处理拦截器的责任链请求
        List<Interceptor> interceptors = Lists.newArrayList();
        for (Interceptor interceptor : interceptors) {
            if (!interceptor.action()) {
                return null;
            }
        }

        // 发起转发路由请求
        byte serializable = Serializer.HESSIAN.getType();
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, gatewayRequestModel.getProvider(), gatewayRequestModel.getVersion());
        GatewayConfigModel gatewayConfig = GatewayServiceConfig.getGatewayConfig(proxyProviderModel);
        if (gatewayConfig == null) {
            return ApiResult.buildFail(CodeEnum.GATEWAY_SERVICE_NOT_EXIST);
        }
        Long timeout = gatewayConfig.getTimeout();
        // default timeout 10s
        if (timeout == null || timeout <= 0) {
            timeout = 10L;
        }
        ClientInvoker clientInvoker = new ClientInvoker(proxyProviderModel, daoLoadBalance, serializable, timeout);
        Object result;
        try {
            result = clientInvoker.invoke(gatewayRequestModel);
        } catch (InterruptedException e) {
            return ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_LIMIT);
        }
        return ApiResult.buildSuccess(result);
    }
}
