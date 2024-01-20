package com.junmo.gateway;

import cn.hutool.core.util.IdUtil;
import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.gateway.limit.Limiter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: sucf
 * @date: 2023/12/28 14:55
 * @description: 转发分发处理器
 */
@RestController
public class Dispatcher {

    private Limiter limiter;

    public Dispatcher(Limiter limiter) {
        this.limiter = limiter;
    }

    /**
     * 网关请求主入口
     *
     * @param proxy
     * @param provider
     * @param version
     * @param request
     * @param response
     */
    @RequestMapping("api/{proxy}/{provider}/{version}")
    public <T> T main(@PathVariable String proxy, @PathVariable String provider, @PathVariable String version, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(version)) {
            return null;
        }
        return doService();
    }

    public <T> T doService() {
        // 先判断限流
        if (!limiter.allow()) {
            return (T) ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_LIMIT);
        }
        // todo 这里应该是一个责任链的方式在处理请求
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
            return (T) ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_ERROR);
        }
        return (T) rpcResponseModel.getReturnValue();
    }

    public RpcResponseModel invoke(RpcRequestModel rpcRequestModel) {
        return null;
    }

}
