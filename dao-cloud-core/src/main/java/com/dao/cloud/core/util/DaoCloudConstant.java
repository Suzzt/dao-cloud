package com.dao.cloud.core.util;


import com.dao.cloud.core.model.Model;
import io.netty.util.AttributeKey;

/**
 * @author: sucf
 * @date: 2022/10/31 17:06
 * @description:
 */
public class DaoCloudConstant {
    public static final String MAGIC_NUMBER = "dao";

    public static final int CENTER_PORT = 5551;

    public static final int GATEWAY_PORT = 6666;

    public static final byte DEFAULT_SERIALIZE = 0;

    public static final String CONFIG = "config";

    public static final String GATEWAY = "gateway";

    public static final String GATEWAY_PROXY = "dao-cloud-gateway";

    public static final AttributeKey<Model> REQUEST_MESSAGE_ATTR_KEY = AttributeKey.valueOf("REQUEST_MESSAGE");
}
