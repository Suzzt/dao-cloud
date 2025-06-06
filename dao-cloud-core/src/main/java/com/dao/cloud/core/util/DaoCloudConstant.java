package com.dao.cloud.core.util;


import com.dao.cloud.core.model.Model;
import io.netty.util.AttributeKey;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/10/31 17:06
 */
public class DaoCloudConstant {
    public static final byte[] MAGIC_NUMBER = {'d', 'a', 'o'};
    public static final int MAGIC_NUMBER_LENGTH = 3;
    public static final int HEARTBEAT_FRAME_LENGTH = 4;
    public static final int MAX_FRAME_LENGTH = 10 * 1024 * 1024;
    public static final byte PROTOCOL_VERSION_1 = 1;
    public static final int CENTER_PORT = 5551;

    public static final int GATEWAY_PORT = 6666;

    public static final byte DEFAULT_SERIALIZE = 0;

    public static final String CONFIG = "config";

    public static final String CONFIGURATION = "configuration";

    public static final String GATEWAY = "gateway";

    public static final String SERVER = "server";

    public static final String CALL = "call";

    public static final String GATEWAY_PROXY = "dao-cloud-gateway";

    public static final AttributeKey<Model> REQUEST_MESSAGE_ATTR_KEY = AttributeKey.valueOf("REQUEST_MESSAGE");

    public static final int SLIDE_WINDOW_COUNT_ALGORITHM = 1;

    public static final int TOKEN_BUCKET_ALGORITHM = 2;

    public static final int LEAKY_BUCKET_ALGORITHM = 3;

    public static final String MACOS_HIDE_FILE_NAME = ".DS_Store";
}
