package com.dao.cloud.core.netty.protocol;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/1/31 15:07
 */
public class HeartbeatPacket extends DaoMessage{
    public HeartbeatPacket() {
        super();
        setMessageType(MessageType.PING_PONG_HEART_BEAT_MESSAGE);
    }
}
