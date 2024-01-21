package com.junmo.gateway;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.boot.bootstrap.unit.Client;
import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.LongPromiseBuffer;
import com.junmo.gateway.auth.Interceptor;
import com.junmo.gateway.limit.Limiter;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

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

        // 处理拦截器的责任链请求
        List<Interceptor> interceptors = Lists.newArrayList();
        for (Interceptor interceptor : interceptors) {
            if(!interceptor.action()){
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
