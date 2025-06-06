package com.dao.cloud.core.netty.serialize.impl;

import com.dao.cloud.core.netty.serialize.DaoSerializer;

import java.io.*;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/1/17 22:17
 */
public class JdkSerializer implements DaoSerializer {
    @Override
    public <T> byte[] serialize(T t) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(t);
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return (T) ois.readObject();
    }
}
