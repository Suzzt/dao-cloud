package com.dao.cloud.core.constant;

import com.dao.cloud.core.model.Model;
import io.netty.util.AttributeKey;

/**
 * @author sucf
 * @date 2025/7/24 23:24:33
 * @since 1.0.0
 * 协议常量
 */
public class Protocol {
    public static final byte[] MAGIC_NUMBER = {'d', 'a', 'o'};
    public static final int MAGIC_NUMBER_LENGTH = 3;
    public static final int HEARTBEAT_FRAME_LENGTH = 4;
    public static final int MAX_FRAME_LENGTH = 10 * 1024 * 1024;
    public static final byte DEFAULT_VERSION = 1;
    public static final byte DEFAULT_SERIALIZE = 0;
    public static final AttributeKey<Model> REQUEST_MESSAGE_ATTR_KEY = AttributeKey.valueOf("REQUEST_MESSAGE");

    private Protocol() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
