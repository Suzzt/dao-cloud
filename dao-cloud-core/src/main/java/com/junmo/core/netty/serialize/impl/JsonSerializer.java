package com.junmo.core.netty.serialize.impl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.netty.serialize.ClassCodec;
import com.junmo.core.netty.serialize.DaoSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author: sucf
 * @date: 2023/1/17 22:24
 * @description:
 */
public class JsonSerializer implements DaoSerializer {
    @Override
    public <T> byte[] serialize(T t) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        String contentJson = gson.toJson(t);
        return contentJson.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classOfT) {
        String contentJson = new String(bytes);
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        T t = gson.fromJson(contentJson, classOfT);
        if (t instanceof RpcRequestModel) {
            RpcRequestModel rpcRequestModel = (RpcRequestModel) t;
            Object[] parameterValueNew = new Object[rpcRequestModel.getParameterTypes().length];
            Object[] parameterValue = rpcRequestModel.getParameterValue();
            Class[] parameterTypes = rpcRequestModel.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class type = parameterTypes[i];
                Object value = parameterValue[i];
                if (value instanceof LinkedTreeMap) {
                    LinkedTreeMap<String, Object> valueMap = (LinkedTreeMap<String, Object>) value;
                    parameterValueNew[i] = gson.fromJson(gson.toJson(valueMap), type);
                }
            }
            rpcRequestModel.setParameterValue(parameterValueNew);
            return (T) rpcRequestModel;
        }
        return t;
    }
}
