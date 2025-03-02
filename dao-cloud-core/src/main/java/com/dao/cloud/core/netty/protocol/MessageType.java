package com.dao.cloud.core.netty.protocol;

import com.dao.cloud.core.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/1/13 17:22
 * dao-cloud transfer protocol type
 */
public class MessageType {
    public static final byte GLOBAL_DAO_EXCEPTION_MESSAGE = -9;
    public static final byte UPLOAD_LOG_MESSAGE = -6;
    public static final byte PING_PONG_HEART_BEAT_MESSAGE = -1;
    public static final byte REGISTRY_REQUEST_MESSAGE = 0;
    public static final byte PULL_REGISTRY_SERVER_REQUEST_MESSAGE = 2;
    public static final byte PULL_REGISTRY_SERVER_RESPONSE_MESSAGE = 3;
    public static final byte SERVICE_RPC_REQUEST_MESSAGE = 4;
    public static final byte SERVICE_RPC_RESPONSE_MESSAGE = 5;
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
    public static final byte GATEWAY_RPC_REQUEST_MESSAGE = 16;
    public static final byte INQUIRE_CLUSTER_FULL_SERVER_CONFIG_REQUEST_MESSAGE = 19;
    public static final byte INQUIRE_CLUSTER_FULL_SERVER_CONFIG_RESPONSE_MESSAGE = 20;
    public static final byte CALL_TREND_RESPONSE_MESSAGE = 21;
    public static final byte INQUIRE_CLUSTER_FULL_CALL_TREND_REQUEST_MESSAGE = 22;
    public static final byte INQUIRE_CLUSTER_FULL_CALL_TREND_RESPONSE_MESSAGE = 23;
    public static final byte PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE = 26;
    public static final byte PULL_CENTER_CONFIGURATION_FILE_INFORMATION_RESPONSE_MESSAGE = 27;
    public static final byte PULL_CENTER_CONFIGURATION_PROPERTY_REQUEST_MESSAGE = 28;
    public static final byte PULL_CENTER_CONFIGURATION_PROPERTY_RESPONSE_MESSAGE = 29;
    public static final byte INQUIRE_CLUSTER_FULL_CONFIGURATION_FILE_REQUEST_MESSAGE = 30;
    public static final byte INQUIRE_CLUSTER_FULL_CONFIGURATION_FILE_RESPONSE_MESSAGE = 31;


    private static final Map<Byte, Class<? extends Model>> MESSAGE_TYPE_MAP = new HashMap<>();

    static {
        MESSAGE_TYPE_MAP.put(GLOBAL_DAO_EXCEPTION_MESSAGE, GlobalExceptionModel.class);
        MESSAGE_TYPE_MAP.put(UPLOAD_LOG_MESSAGE, LogModel.class);
        MESSAGE_TYPE_MAP.put(PING_PONG_HEART_BEAT_MESSAGE, HeartbeatModel.class);
        MESSAGE_TYPE_MAP.put(REGISTRY_REQUEST_MESSAGE, RegisterProviderModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_SERVER_REQUEST_MESSAGE, ProxyProviderModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_SERVER_RESPONSE_MESSAGE, ProxyProviderServerModel.class);
        MESSAGE_TYPE_MAP.put(SERVICE_RPC_REQUEST_MESSAGE, RpcRequestModel.class);
        MESSAGE_TYPE_MAP.put(SERVICE_RPC_RESPONSE_MESSAGE, RpcResponseModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_CONFIG_REQUEST_MESSAGE, ProxyConfigModel.class);
        MESSAGE_TYPE_MAP.put(PULL_REGISTRY_CONFIG_RESPONSE_MESSAGE, ConfigModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE, ClusterInquireMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_NODE_RESPONSE_MESSAGE, ClusterCenterNodeModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CONFIG_REQUEST_MESSAGE, ConfigMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CONFIG_RESPONSE_MESSAGE, FullConfigModel.class);
        MESSAGE_TYPE_MAP.put(SYNC_CLUSTER_SERVER_REQUEST_MESSAGE, AbstractShareClusterRequestModel.class);
        MESSAGE_TYPE_MAP.put(SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, ClusterSyncDataResponseModel.class);
        MESSAGE_TYPE_MAP.put(GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE, GatewayConfigPullMarkModel.class);
        MESSAGE_TYPE_MAP.put(GATEWAY_REGISTER_ALL_SERVER_RESPONSE_MESSAGE, GatewayServiceNodeModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_SERVER_CONFIG_REQUEST_MESSAGE, ServerConfigPullMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_SERVER_CONFIG_RESPONSE_MESSAGE, ServerConfigModel.class);
        MESSAGE_TYPE_MAP.put(GATEWAY_RPC_REQUEST_MESSAGE, GatewayRequestModel.class);
        MESSAGE_TYPE_MAP.put(CALL_TREND_RESPONSE_MESSAGE, CallTrendModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CALL_TREND_REQUEST_MESSAGE, CallTrendPullMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CALL_TREND_RESPONSE_MESSAGE, CallTrendFullModel.class);
        MESSAGE_TYPE_MAP.put(PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE, ConfigurationFileInformationRequestModel.class);
        MESSAGE_TYPE_MAP.put(PULL_CENTER_CONFIGURATION_FILE_INFORMATION_RESPONSE_MESSAGE, ConfigurationFileInformationResponseModel.class);
        MESSAGE_TYPE_MAP.put(PULL_CENTER_CONFIGURATION_PROPERTY_REQUEST_MESSAGE, ConfigurationPropertyRequestModel.class);
        MESSAGE_TYPE_MAP.put(PULL_CENTER_CONFIGURATION_PROPERTY_RESPONSE_MESSAGE, ConfigurationPropertyResponseModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CONFIGURATION_FILE_REQUEST_MESSAGE, ConfigurationFilePullMarkModel.class);
        MESSAGE_TYPE_MAP.put(INQUIRE_CLUSTER_FULL_CONFIGURATION_FILE_RESPONSE_MESSAGE, ConfigurationFileResponseModel.class);
    }

    public static Class<? extends Model> getMessageModel(byte messageType) {
        return MESSAGE_TYPE_MAP.get(messageType);
    }
}
