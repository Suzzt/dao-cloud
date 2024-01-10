package com.junmo.core.netty.protocol;

import com.junmo.core.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/1/13 17:22
 * @description:
 */
public class MessageType {
    public static final byte PING_PONG_HEART_BEAT_MESSAGE = -1;
    public static final byte REGISTRY_REQUEST_MESSAGE = 0;
    public static final byte PULL_REGISTRY_SERVER_REQUEST_MESSAGE = 2;
    public static final byte PULL_REGISTRY_SERVER_RESPONSE_MESSAGE = 3;
    public static final byte RPC_REQUEST_MESSAGE = 4;
    public static final byte RPC_RESPONSE_MESSAGE = 5;
    public static final byte PULL_REGISTRY_CONFIG_REQUEST_MESSAGE = 6;
    public static final byte PULL_REGISTRY_CONFIG_RESPONSE_MESSAGE = 7;
    public static final byte INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE = 8;
    public static final byte INQUIRE_CLUSTER_NODE_RESPONSE_MESSAGE = 9;
    public static final byte INQUIRE_CLUSTER_FULL_CONFIG_REQUEST_MESSAGE = 10;
    public static final byte INQUIRE_CLUSTER_FULL_CONFIG_RESPONSE_MESSAGE = 11;
    public static final byte SYNC_CLUSTER_SERVER_REQUEST_MESSAGE = 12;
    public static final byte SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE = 13;
    public static final byte GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE = 14;
    public static final byte GATEWAY_REGISTER_ALL_SERVER_RESPONSE_MESSAGE = 15;

    private static final Map<Byte, Class<? extends Model>> MESSAGE_TYPE_MAP = new HashMap<>();

    static {
        MESSAGE_TYPE_MAP.put(PING_PONG_HEART_BEAT_MESSAGE, HeartbeatModel.class);
        MESSAGE_TYPE_MAP.put(REGISTRY_REQUEST_MESSAGE, RegisterProviderModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_SERVER_REQUEST_MESSAGE, ProxyProviderModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_SERVER_RESPONSE_MESSAGE, ProxyProviderServerModel.class);
        MESSAGE_TYPE_MAP.put(RPC_REQUEST_MESSAGE, RpcRequestModel.class);
        MESSAGE_TYPE_MAP.put(RPC_RESPONSE_MESSAGE, RpcResponseModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_CONFIG_REQUEST_MESSAGE, ProxyConfigModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_CONFIG_RESPONSE_MESSAGE, ConfigModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE, ClusterInquireMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_NODE_RESPONSE_MESSAGE, ClusterCenterNodeModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CONFIG_REQUEST_MESSAGE, ConfigMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CONFIG_RESPONSE_MESSAGE, FullConfigModel.class);
        MESSAGE_TYPE_MAP.put(SYNC_CLUSTER_SERVER_REQUEST_MESSAGE, ClusterSyncDataRequestModel.class);
        MESSAGE_TYPE_MAP.put(SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, ClusterSyncDataResponseModel.class);
        MESSAGE_TYPE_MAP.put(GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE, GatewayPullServiceMarkModel.class);
        MESSAGE_TYPE_MAP.put(GATEWAY_REGISTER_ALL_SERVER_RESPONSE_MESSAGE, GatewayServiceNodeModel.class);
    }

    public static Class<? extends Model> getMessageModel(byte messageType) {
        return MESSAGE_TYPE_MAP.get(messageType);
    }
}
