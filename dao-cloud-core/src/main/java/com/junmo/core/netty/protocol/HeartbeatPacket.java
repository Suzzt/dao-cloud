package com.junmo.core.netty.protocol;

/**
 * @author: sucf
 * @date: 2023/1/31 15:07
 * @description:
 */
public class HeartbeatPacket extends DaoMessage{
    public HeartbeatPacket() {
        super();
        setMessageType(MessageType.PING_PONG_HEART_BEAT_MESSAGE);
    }
}
