package com.junmo.center.core;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import com.junmo.center.core.cluster.ClusterCenterConnector;
import com.junmo.center.core.cluster.DataSyncTask;
import com.junmo.center.core.handler.InquireClusterCenterResponseHandler;
import com.junmo.center.core.handler.PullConfigResponseHandler;
import com.junmo.core.MainProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.expand.Persistence;
import com.junmo.core.model.*;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.ThreadPoolFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/3/12 22:27
 * @description: center cluster manager
 */
@Slf4j
public class CenterClusterManager {

    private static Persistence persistence;

    /**
     * 询问cluster节点ip
     */
    public static String inquireIpAddress;

    /**
     * 异步线程池执行center cluster数据同步任务
     */
    private static ThreadPoolExecutor syncDataThreadPoolExecutor = ThreadPoolFactory.makeThreadPool("center-cluster-data-sync", 1, 20);

    /**
     * history cluster info
     * key: ip
     * value: cluster interaction connector. in each connector there is a connection request that is constantly likely to be retried
     */
    private static final Map<String, ClusterCenterConnector> ALL_HISTORY_CLUSTER_MAP = Maps.newHashMap();

    /**
     * 载入持久化配置信息
     *
     * @param persistence
     */
    public static void setPersistence(Persistence persistence) {
        CenterClusterManager.persistence = persistence;
    }

    /**
     * alive node
     *
     * @param localAddressIp 自身节点ip
     * @return
     */
    public static Set<String> aliveNode(String localAddressIp) {
        Set<String> set = new HashSet<>();
        set.add(localAddressIp);
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector connector = entry.getValue();
            if (connector.isActive()) {
                set.add(entry.getKey());
            }
        }
        return set;
    }

    /**
     * alive node size
     *
     * @return
     */
    public static int aliveNodeSize() {
        int i = 0;
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector connector = entry.getValue();
            if (connector.isActive()) {
                i++;
            }
        }
        return i;
    }

    /**
     * cluster start
     *
     * @throws InterruptedException
     */
    public static void start() throws InterruptedException {
        // get cluster alive node
        Set<String> aliveNodes = inquire();
        // init cluster channel
        for (String aliveNode : aliveNodes) {
            joinCluster(aliveNode);
        }
        // sync init config
        for (String aliveNode : aliveNodes) {
            loadConfig(aliveNode);
        }
    }

    /**
     * load the config that is now overwritten in the cluster
     *
     * @param ip
     * @throws InterruptedException
     */
    private static void loadConfig(String ip) throws InterruptedException {
        ClusterCenterConnector clusterCenterConnector = ALL_HISTORY_CLUSTER_MAP.get(ip);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.INQUIRE_CLUSTER_FULL_CONFIG_REQUEST_MESSAGE, MainProperties.serialize, new ConfigMarkModel());
        Promise<FullConfigModel> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
        PullConfigResponseHandler.promise = promise;
        clusterCenterConnector.getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send full config data error", future.cause());
            }
        });
        if (!promise.await(10, TimeUnit.SECONDS)) {
            log.error("<<<<<<<<<<<<<< get full config data timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
        }
        if (promise.isSuccess()) {
            List<ConfigModel> configModels = promise.getNow().getConfigModels();
            for (ConfigModel configModel : configModels) {
                persistence.storage(configModel);
            }
        } else {
            throw new DaoException(promise.cause());
        }
    }

    /**
     * synchronized server info to cluster
     *
     * @param type
     * @param registerProviderModel
     */
    public static void syncRegisterToCluster(byte type, RegisterProviderModel registerProviderModel) {
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            ClusterSyncDataRequestModel clusterSyncDataRequestModel = new ClusterSyncDataRequestModel();
            clusterSyncDataRequestModel.setType(type);
            clusterSyncDataRequestModel.setRegisterProviderModel(registerProviderModel);
            clusterCenterConnector.syncData(clusterSyncDataRequestModel);
        }
    }

    /**
     * synchronized config info to cluster
     *
     * @param type
     * @param proxyConfigModel
     * @param configJson
     */
    public static void syncConfigToCluster(byte type, ProxyConfigModel proxyConfigModel, String configJson) {
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            ClusterSyncDataRequestModel clusterSyncDataRequestModel = new ClusterSyncDataRequestModel();
            clusterSyncDataRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
            clusterSyncDataRequestModel.setType(type);
            clusterSyncDataRequestModel.setProxyConfigModel(proxyConfigModel);
            clusterSyncDataRequestModel.setConfigJson(configJson);
            DataSyncTask dataSyncTask = new DataSyncTask(clusterCenterConnector, clusterSyncDataRequestModel);
            syncDataThreadPoolExecutor.execute(dataSyncTask);
        }
    }

    /**
     * join cluster
     *
     * @param ip
     */
    public static void joinCluster(String ip) {
        log.info("add a new or heartbeat (ip = {}) node cluster", ip);
        if (ALL_HISTORY_CLUSTER_MAP.get(ip) == null) {
            ALL_HISTORY_CLUSTER_MAP.put(ip, new ClusterCenterConnector(ip));
        }
    }

    /**
     * down center
     *
     * @param ip
     */
    public static void down(String ip) {
        ClusterCenterConnector clusterCenterConnector = ALL_HISTORY_CLUSTER_MAP.remove(ip);
        if (clusterCenterConnector != null) {
            log.info("down center node(ip = {})", ip);
        }
    }

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
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(inquireIpAddress, DaoCloudConstant.CENTER_CENTER_PORT);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ProtocolFrameDecoder()).addLast(new DaoMessageCoder()).addLast(new InquireClusterCenterResponseHandler());
            }
        });
        Channel channel = bootstrap.connect().sync().channel();
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
            log.error("<<<<<<<<<<<<<< inquire cluster ips timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
        }
        channel.close().sync();
        group.shutdownGracefully().sync();
        if (promise.isSuccess()) {
            Set<String> aliveNodes = promise.getNow().getClusterNodes();
            return aliveNodes;
        } else {
            throw new DaoException(promise.cause());
        }
    }
}
