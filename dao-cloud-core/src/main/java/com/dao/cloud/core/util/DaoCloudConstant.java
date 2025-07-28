package com.dao.cloud.core.util;


import com.dao.cloud.core.model.Model;
import io.netty.util.AttributeKey;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/10/31 17:06
 */
public class DaoCloudConstant {
    public static final byte PROTOCOL_VERSION_1 = 1;
    public static final byte DEFAULT_SERIALIZE = 0;

    public static final AttributeKey<Model> REQUEST_MESSAGE_ATTR_KEY = AttributeKey.valueOf("REQUEST_MESSAGE");
}
