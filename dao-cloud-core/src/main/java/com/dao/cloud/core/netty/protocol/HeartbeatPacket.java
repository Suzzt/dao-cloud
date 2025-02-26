package com.dao.cloud.core.netty.protocol;

/**
 * @author sucf
 * @since 1.0
 */
public class HeartbeatPacket extends DaoMessage{
    public HeartbeatPacket() {
        super();
        setMessageType(MessageType.PING_PONG_HEART_BEAT_MESSAGE);
    }
}
