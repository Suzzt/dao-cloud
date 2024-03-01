package com.dao.cloud.core.netty.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.dao.cloud.core.netty.serialize.DaoSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: sucf
 * @date: 2023/2/20 09:40
 * @description:
 */
public class HessianSerializer implements DaoSerializer {
    @Override
    public <T> byte[] serialize(T t) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
        try {
            hessian2Output.setSerializerFactory(new SerializerFactory());
            hessian2Output.writeObject(t);
        } finally {
            byteArrayOutputStream.close();
            hessian2Output.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessianInput = new Hessian2Input(byteArrayInputStream);
        try {
            hessianInput.setSerializerFactory(new SerializerFactory());
            return (T) hessianInput.readObject(classType);
        } finally {
            byteArrayInputStream.close();
            hessianInput.close();
        }
    }
}
