package com.junmo.center.core;

import com.google.common.collect.Maps;
import com.junmo.center.core.cluster.ClusterCenterConnector;
import com.junmo.center.core.handler.InquireClusterCenterResponseHandler;
import com.junmo.core.MainProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.*;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/3/12 22:27
 * @description: center cluster
 */
@Slf4j
public class CenterClusterManager {

    public static String inquireIpAddress;

    /**
     * cluster info
     * key: ip
     * value: cluster interaction connector
     */
    private static Map<String, ClusterCenterConnector> clusterMap = Maps.newHashMap();

    public static Set<String> aliveNode(String localAddressIp) {
        Set<String> set = new HashSet<>();
        set.add(localAddressIp);
        for (Map.Entry<String, ClusterCenterConnector> entry : clusterMap.entrySet()) {
            set.add(entry.getKey());
        }
        return set;
    }

    /**
     * cluster start
     *
     * @throws InterruptedException
     */
    public static void start() throws InterruptedException {
        // get cluster alive node
        Set<String> aliveNodes = inquire();
        for (String aliveNode : aliveNodes) {
            joinCluster(aliveNode);
        }
    }

    /**
     * synchronized server to cluster
     */
    public static void syncRegisterToCluster(byte type, RegisterProviderModel registerProviderModel) {
        for (Map.Entry<String, ClusterCenterConnector> entry : clusterMap.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            ClusterSyncDataModel clusterSyncDataModel = new ClusterSyncDataModel();
            clusterSyncDataModel.setType(type);
            clusterSyncDataModel.setRegisterProviderModel(registerProviderModel);
            clusterCenterConnector.syncData(clusterSyncDataModel);
        }
    }

    public static void syncConfigToCluster(byte type, ProxyConfigModel proxyConfigModel, String configJson) {
        for (Map.Entry<String, ClusterCenterConnector> entry : clusterMap.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            ClusterSyncDataModel clusterSyncDataModel = new ClusterSyncDataModel();
            clusterSyncDataModel.setType(type);
            clusterSyncDataModel.setProxyConfigModel(proxyConfigModel);
            clusterSyncDataModel.setConfigJson(configJson);
            clusterCenterConnector.syncData(clusterSyncDataModel);
        }
    }

    /**
     * join cluster
     *
     * @param ip
     */
    public static void joinCluster(String ip) {
        log.info("add a new or heartbeat (ip = {}) node cluster", ip);
        if (clusterMap.get(ip) == null) {
            clusterMap.put(ip, new ClusterCenterConnector(ip, true));
        }
    }

    /**
     * down center
     *
     * @param ip
     */
    public static void remove(String ip) {
        log.info("down center node(ip = {})", ip);
        ClusterCenterConnector clusterCenterConnector = clusterMap.remove(ip);
        clusterCenterConnector.cancel();
    }

//    public static class SyncServerHandler {
//        /**
//         * notify other cluster nodes
//         */
//        public static void notice(ClusterSyncDataModel clusterSyncDataModel) {
//            Set<Map.Entry<String, ClusterCenterConnector>> set = clusterMap.entrySet();
//            for (Map.Entry<String, ClusterCenterConnector> entry : set) {
//                ClusterCenterConnector connector = entry.getValue();
//                connector.getChannel().writeAndFlush(clusterSyncDataModel).addListeners(future -> {
//                    if (!future.isSuccess()) {
//                        log.error("send sync server error", future.cause());
//                    }
//                });
//            }
//        }
//
//        /**
//         * receive processing
//         *
//         * @param clusterSyncDataModel
//         */
//        public static void accept(ClusterSyncDataModel clusterSyncDataModel) {
//            RegisterProviderModel registerProviderModel = clusterSyncDataModel.getRegisterProviderModel();
//            if (clusterSyncDataModel.getType() == (byte) -1) {
//                RegisterCenterManager.delete(registerProviderModel);
//            } else {
//                RegisterCenterManager.register(registerProviderModel);
//            }
//        }
//
//    }

    /**
     * inquire cluster ip
     * 由集群配置ip来获取集群中的所有center节点.
     * 询问方案
     * 1.准备新center集群节点.
     * 2.根据ip连接目标center(见 DaoCloudClusterCenterProperties.class 配置).
     * 3.center ip 收集整个集群各个center node，拿出所有由心跳构成的长连接(tcp)，去重汇聚.
     * 注意：这里的所获取的node不能保证一定全部存活，只能保证请求时快照存活的node.
     * <p>
     * inquire cluster ip
     * retrieve all center nodes in a cluster based on the cluster's configured ip.
     * inquiry plan
     * prepare a new center cluster node.
     * connect to the target center based on the ip configuration (see DaoCloudClusterCenterProperties.class).
     * collect all center nodes in the entire cluster based in the center ip, remove duplicate nodes, and aggregate all long connections (tcp) based on heartbeats.
     * note: the obtained nodes cannot be guaranteed to be all active, only nodes that are alive at the time of the snapshot request can be guaranteed to be saved.
     *
     * @return
     */
    public static Set<String> inquire() throws InterruptedException {
        ClusterCenterConnector connector = new ClusterCenterConnector(inquireIpAddress, false);
        Channel channel = connector.getChannel();
        ClusterInquireMarkModel clusterInquireMarkModel = new ClusterInquireMarkModel();
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE, MainProperties.serialize, clusterInquireMarkModel);
        DefaultPromise<ClusterCenterNodeModel> promise = new DefaultPromise<>(channel.eventLoop());
        InquireClusterCenterResponseHandler.promise = promise;
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<< send inquire cluster node ip error >>>>>>>>>>>", future.cause());
                promise.setFailure(future.cause());
            }
        });
        if (!promise.await(3, TimeUnit.SECONDS)) {
            throw new DaoException(promise.cause());
        }
        if (promise.isSuccess()) {
            Set<String> aliveNodes = promise.getNow().getClusterNodes();
            // aliveNodes.add(inquireIpAddress);
            return aliveNodes;
        } else {
            throw new DaoException(promise.cause());
        }
    }
}
