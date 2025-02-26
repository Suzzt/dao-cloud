package com.dao.cloud.core.netty.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author sucf
 * @since 1.0
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024 * 10, 6, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}