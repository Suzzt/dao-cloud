package com.dao.cloud.starter.unit;

import com.dao.cloud.core.model.RpcRequestModel;
import com.dao.cloud.core.model.RpcResponseModel;
import com.dao.cloud.starter.bootstrap.RpcProviderBootstrap;
import java.util.Map;

/**
 * @author wuzhenhong
 * @since 1.0.0
 * @date 2024/7/17 16:52
 */
public class CallTrendServiceInvoker extends ServiceInvoker {

    private Map<String, CallTrendTimerTask> interfacesCallTrendMap;

    public CallTrendServiceInvoker(byte serialized, Object serviceBean, Map<String, CallTrendTimerTask> interfacesCallTrendMap) {
        super(serialized, serviceBean);
        this.interfacesCallTrendMap = interfacesCallTrendMap;
    }

    @Override
    public RpcResponseModel doInvoke(RpcRequestModel requestModel) {

        String methodName = requestModel.getMethodName();
        Class<?>[] parameterTypes = requestModel.getParameterTypes();
        String methodSign = RpcProviderBootstrap.methodToString(methodName, parameterTypes);
        CallTrendTimerTask callTrendTimerTask = this.interfacesCallTrendMap.get(methodSign);
        if (callTrendTimerTask != null) {
            callTrendTimerTask.increment();
        }
        return super.doInvoke(requestModel);
    }

}
