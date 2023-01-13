package com.junmo.boot.serializer;

/**
 * @author: sucf
 * @date: 2022/12/29 21:38
 * @description:
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param t
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
