package com.junmo.gateway;

import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.gateway.limit.Limiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2023/12/28 14:55
 * @description: 转发分发处理器
 */
@Component
public class Dispatcher {

    private Limiter limiter;

    @Autowired
    public Dispatcher(Limiter limiter) {
        this.limiter = limiter;
    }

    public ApiResult doService() {
        // 先判断限流
        if (!limiter.allow()) {
            return ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_LIMIT);
        }
        // todo 这里应该是一个责任链的方式在处理请求
        return null;
    }

    public RpcResponseModel invoke(RpcRequestModel rpcRequestModel) {
        // 这里要加载进来所有的center中proxy服务
        return null;
    }

}
