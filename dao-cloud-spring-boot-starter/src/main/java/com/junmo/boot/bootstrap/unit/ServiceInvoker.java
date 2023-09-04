package com.junmo.boot.bootstrap.unit;

import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author: sucf
 * @date: 2023/2/15 22:33
 * @description: rpc service invoke handler
 */
@Slf4j
public class ServiceInvoker {
    private byte serialized;

    private Object serviceBean;

    public byte getSerialized() {
        return this.serialized;
    }

    public ServiceInvoker(byte serialized, Object serviceBean) {
        this.serialized = serialized;
        this.serviceBean = serviceBean;
    }

    /**
     * invoke method
     *
     * @param requestModel
     * @return
     */
    public RpcResponseModel doInvoke(RpcRequestModel requestModel) {
        //  make response
        RpcResponseModel responseModel = new RpcResponseModel();
        responseModel.setSequenceId(requestModel.getSequenceId());

        if (serviceBean == null) {
            responseModel.setErrorMessage("provider not exists method");
            return responseModel;
        }

        try {
            // invoke method
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = requestModel.getMethodName();
            Class<?>[] parameterTypes = requestModel.getParameterTypes();
            Object[] parameters = requestModel.getParameterValue();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceBean, parameters);
            responseModel.setReturnValue(result);
        } catch (Throwable t) {
            log.error("<<<<<<<<<<< dao-cloud provider invoke method error >>>>>>>>>>>>", t);
            responseModel.setErrorMessage(t.getMessage());
        }
        return responseModel;
    }
}
