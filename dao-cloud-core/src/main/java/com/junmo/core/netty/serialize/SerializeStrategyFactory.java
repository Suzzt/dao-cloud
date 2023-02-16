package com.junmo.core.netty.serialize;

import com.junmo.core.enums.Serializer;

/**
 * @author: sucf
 * @date: 2023/1/18 13:55
 * @description:
 */
public class SerializeStrategyFactory {

    public static byte JDK_SERIALIZER = 0;

    public static byte JSON_SERIALIZER = 1;

    public static DaoSerializer getSerializer(byte type) {
        Serializer[] values = Serializer.values();
        for (Serializer serializer : values) {
            if (serializer.getType() == type) {
                return serializer.getDaoSerializer();
            }
        }
        // default
        return Serializer.JDK.getDaoSerializer();
    }

    public static DaoSerializer getSerializer(String name) {
        Serializer[] serializerStrategies = Serializer.values();
        for (Serializer serializer : serializerStrategies) {
            if (serializer.getName().equals(name)) {
                return serializer.getDaoSerializer();
            }
        }
        // default
        return Serializer.JDK.getDaoSerializer();
    }

    public static Byte getSerializeType(String name) {
        Serializer[] serializerStrategies = Serializer.values();
        for (Serializer serializer : serializerStrategies) {
            if (serializer.getName().equals(name)) {
                return serializer.getType();
            }
        }
        // default
        return Serializer.JDK.getType();
    }

}
