package com.dao.cloud.core.netty.serialize;

import com.dao.cloud.core.enums.Serializer;

/**
 * @author sucf
 * @since 1.0
 */
public class SerializeStrategyFactory {

    public final static byte HESSIAN = 0;

    public final static byte JDK = 1;

    public final static byte JSON = 2;

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
