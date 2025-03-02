package com.dao.cloud.starter.unit;

import com.dao.cloud.core.model.ProxyConfigModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/27 16:35
 * call back method
 */
public abstract class ConfigCallBack<T> {

    private final Class<T> clazz;

    protected ConfigCallBack() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.clazz = (Class<T>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }
    }

    public Class<T> getClazz() {
        return clazz;
    }

    /**
     * 如果你想感知到配置信息发生变化的时刻，并且在变化时做某些事，请实现这个回调函数
     *
     * @param proxyConfigModel 配置key
     * @param obj              对象
     */
    public abstract void callback(ProxyConfigModel proxyConfigModel, T obj);
}
