package com.junmo.core.netty.serialize;

import com.junmo.core.enums.Serializer;

/**
 * @author: sucf
 * @date: 2023/1/18 13:55
 * @description:
 */
public class SerializeStrategyFactory {

    public static byte HESSIAN = 0;

    public static byte JDK = 1;

    public static byte JSON = 2;

    public static DaoSerializer getSerializer(byte type) {
        Serializer[] values = Serializer.values();
        for (Serializer serializer : values) {
            if (serializer.getType() == type) {
                return serializer.getDaoSerializer();
            }
        }
        // default
        return Serializer.HESSIAN.getDaoSerializer();
    }

    public static DaoSerializer getSerializer(String name) {
        Serializer[] serializerStrategies = Serializer.values();
        for (Serializer serializer : serializerStrategies) {
            if (serializer.getName().equals(name)) {
                return serializer.getDaoSerializer();
            }
        }
        // default
        return Serializer.HESSIAN.getDaoSerializer();
    }

    public static Byte getSerializeType(String name) {
        Serializer[] serializerStrategies = Serializer.values();
        for (Serializer serializer : serializerStrategies) {
            if (serializer.getName().equals(name)) {
                return serializer.getType();
            }
        }
        // default
        return Serializer.HESSIAN.getType();
    }

}
