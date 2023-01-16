package com.junmo.core.netty.protocol;

import com.junmo.core.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/1/13 17:22
 * @description:
 */
public class MessageModelTypeManager {
    public static final byte PING_HEART_BEAT_MESSAGE = -2;
    public static final byte PONG_HEART_BEAT_MESSAGE = -1;
    public static final byte REGISTRY_REQUEST_MESSAGE = 0;
    public static final byte REGISTRY_RESPONSE_MESSAGE = 1;
    public static final byte POLL_REGISTRY_SERVER_REQUEST_MESSAGE = 2;
    public static final byte POLL_REGISTRY_SERVER_RESPONSE_MESSAGE = 3;





    private static final Map<Byte, Class<? extends Model>> MESSAGE_TYPE_MAP = new HashMap<>();

    static {
        MESSAGE_TYPE_MAP.put(PING_HEART_BEAT_MESSAGE, PingPongModel.class);
        MESSAGE_TYPE_MAP.put(PONG_HEART_BEAT_MESSAGE, PingPongModel.class);
        MESSAGE_TYPE_MAP.put(REGISTRY_REQUEST_MESSAGE, RegisterModel.class);
        MESSAGE_TYPE_MAP.put(REGISTRY_RESPONSE_MESSAGE, RegisterServerModel.class);
        MESSAGE_TYPE_MAP.put(POLL_REGISTRY_SERVER_REQUEST_MESSAGE, RegisterPollModel.class);
        MESSAGE_TYPE_MAP.put(POLL_REGISTRY_SERVER_RESPONSE_MESSAGE, RegisterServerModel.class);
    }

    public static Class<? extends Model> getMessageModel(byte messageType) {
        return MESSAGE_TYPE_MAP.get(messageType);
    }
}
