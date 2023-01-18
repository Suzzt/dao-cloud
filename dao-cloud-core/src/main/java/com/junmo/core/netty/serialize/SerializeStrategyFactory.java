package com.junmo.core.netty.serialize;

import com.junmo.core.enums.SerializerStrategy;

/**
 * @author: sucf
 * @date: 2023/1/18 13:55
 * @description:
 */
public class SerializeStrategyFactory {

    public static byte JDK_SERIALIZER = 0;

    public static byte JSON_SERIALIZER = 1;

    public static DaoSerializer getSerializer(byte type) {
        SerializerStrategy[] serializerStrategies = SerializerStrategy.values();
        for (SerializerStrategy serializerStrategy : serializerStrategies) {
            if (serializerStrategy.getType() == type) {
                return serializerStrategy.getDaoSerializer();
            }
        }
        // default
        return SerializerStrategy.JDK.getDaoSerializer();
    }

    public static DaoSerializer getSerializer(String name) {
        SerializerStrategy[] serializerStrategies = SerializerStrategy.values();
        for (SerializerStrategy serializerStrategy : serializerStrategies) {
            if (serializerStrategy.getName().equals(name)) {
                return serializerStrategy.getDaoSerializer();
            }
        }
        // default
        return SerializerStrategy.JDK.getDaoSerializer();
    }

    public static Byte getSerializeType(String name) {
        SerializerStrategy[] serializerStrategies = SerializerStrategy.values();
        for (SerializerStrategy serializerStrategy : serializerStrategies) {
            if (serializerStrategy.getName().equals(name)) {
                return serializerStrategy.getType();
            }
        }
        // default
        return SerializerStrategy.JDK.getType();
    }
}
