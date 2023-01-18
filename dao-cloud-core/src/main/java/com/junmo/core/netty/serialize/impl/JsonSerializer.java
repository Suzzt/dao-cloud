package com.junmo.core.netty.serialize.impl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        return gson.fromJson(contentJson, classOfT);
    }
}
