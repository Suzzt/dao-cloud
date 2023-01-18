package com.junmo.core.enums;

import com.junmo.core.netty.serialize.DaoSerializer;
import com.junmo.core.netty.serialize.SerializeStrategyFactory;
import com.junmo.core.netty.serialize.impl.JdkSerializer;
import com.junmo.core.netty.serialize.impl.JsonSerializer;

/**
 * @author: sucf
 * @date: 2023/1/18 14:34
 * @description:
 */
public enum SerializerStrategy {
    JDK(SerializeStrategyFactory.JDK_SERIALIZER, "jdk", new JdkSerializer()),
    JSON(SerializeStrategyFactory.JSON_SERIALIZER, "json", new JsonSerializer());

    /**
     * type
     */
    private byte type;

    /**
     * name
     */
    private String name;

    /**
     * dao serializer
     */
    private DaoSerializer daoSerializer;

    SerializerStrategy(byte type, String name, DaoSerializer daoSerializer) {
        this.type = type;
        this.name = name;
        this.daoSerializer = daoSerializer;
    }

    public byte getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public DaoSerializer getDaoSerializer() {
        return daoSerializer;
    }
}
