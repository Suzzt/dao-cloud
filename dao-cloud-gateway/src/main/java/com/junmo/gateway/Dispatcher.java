package com.junmo.gateway;

import com.junmo.core.ApiResult;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;

/**
 * @author: sucf
 * @date: 2023/12/28 14:55
 * @description: 转发分发处理器
 */
public class Dispatcher {

    public void intercept() {
        // todo 先判断限流

        // todo 这里应该是一个责任链的方式在处理请求
    }


    public ApiResult doService() {
        return null;
    }

    public RpcResponseModel invoke(RpcRequestModel rpcRequestModel) {
        // 这里要加载进来所有的center中proxy服务
        return null;
    }

}
