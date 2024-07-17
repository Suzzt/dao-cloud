package com.dao.cloud.starter.unit;

import com.dao.cloud.core.model.RpcRequestModel;
import com.dao.cloud.core.model.RpcResponseModel;
import com.dao.cloud.starter.bootstrap.RpcProviderBootstrap;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author wuzhenhong
 * @date 2024/7/17 16:52
 */
public class MethodInvokerCountInvoker extends ServiceInvoker {

    private ServiceInvoker nextInvoker;
    private Map<String, CallTrendTimerTask> interfacesCallTrendMap;

    public MethodInvokerCountInvoker(ServiceInvoker nextInvoker, Map<String, CallTrendTimerTask> interfacesCallTrendMap) {
        super(nextInvoker.getSerialized(), nextInvoker.getServiceBean());
        this.nextInvoker = nextInvoker;
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
        return this.nextInvoker.doInvoke(requestModel);
    }

}
