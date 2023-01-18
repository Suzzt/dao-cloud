package com.junmo.core.netty.serialize;

import java.io.IOException;

/**
 * @author: sucf
 * @date: 2022/12/29 21:38
 * @description:
 */
public interface DaoSerializer {
    /**
     * encode
     *
     * @param t
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T t) throws IOException;

    /**
     * decode
     * @param bytes
     * @param classType
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException, ClassNotFoundException;
}
