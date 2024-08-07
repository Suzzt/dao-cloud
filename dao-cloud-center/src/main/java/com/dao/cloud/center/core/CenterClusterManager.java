package com.dao.cloud.center.core;

import cn.hutool.core.util.IdUtil;
import com.dao.cloud.center.core.cluster.ClusterCenterConnector;
import com.dao.cloud.center.core.cluster.DataSyncTask;
import com.dao.cloud.center.core.handler.*;
import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.*;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.DaoMessageCoder;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.netty.protocol.ProtocolFrameDecoder;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/3/12 22:27
 * @description: center cluster manager
 */
@Slf4j
public class CenterClusterManager {

    private final static int SYNC_DATA_REQUEST_TIMEOUT = 30;

    private static Persistence persistence;

    /**
     * 询问cluster节点ip
     */
    public static String inquireIpAddress;

    /**
     * 异步线程池执行center cluster数据同步任务
     */
    private final static ThreadPoolExecutor SYNC_DATA_THREAD_POOL_EXECUTOR = ThreadPoolFactory.makeThreadPool("center-cluster-data-sync", 1, 20);

    /**
     * history cluster info
     * key: ip
     * value: cluster interaction connector. in each connector there is a connection request that is constantly likely to be retried
     */
    private static final Map<String, ClusterCenterConnector> ALL_HISTORY_CLUSTER_MAP = Maps.newConcurrentMap();

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
        // count yourself in the count
        int i = 1;
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

        // join cluster heartbeat
        for (String aliveNode : aliveNodes) {
            joinCluster(aliveNode, true);
        }

        // clear local config, This is a dangerous operation!
        persistence.clear();
        log.info("clear local config data");

        // sync overwrite config information
        // todo 这里初始化数据时，整个集群需要进入保护状态，不然数据一定会有不一致的风险! 要建立一个拦截器，当节点处于不稳定或初始化阶段时，把集群中传过来的信息保存到文件中，然后慢慢消费这些请求，注意这个请求有先后顺序！
        if (!CollectionUtils.isEmpty(aliveNodes)) {
            log.info("Synchronize data from other cluster nodes (Waiting......)");
            Iterator<String> iterator = aliveNodes.iterator();
            String node = iterator.next();
            // system config
            loadConfig(node);
            // gateway service config
            loadGatewayConfig(node);
            // server config
            loadServerConfig(node);
            // call trend data
            loadCallTrend(node);
            log.info("Synchronize data from other cluster nodes (Finish)");
        }
    }

    private static void loadServerConfig(String ip) throws InterruptedException {
        ClusterCenterConnector clusterCenterConnector = ALL_HISTORY_CLUSTER_MAP.get(ip);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.INQUIRE_CLUSTER_FULL_SERVER_CONFIG_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new ServerConfigPullMarkModel());
        Promise<ServerConfigModel> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
        CenterClusterServerConfigResponseMessageHandler.promise = promise;
        clusterCenterConnector.getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send server config data error", future.cause());
            }
        });
        if (!promise.await(SYNC_DATA_REQUEST_TIMEOUT, TimeUnit.SECONDS)) {
            log.error("<<<<<<<<<<<<<< Pull service configuration request timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
        }
        if (promise.isSuccess()) {
            ServerConfigModel serverConfigModel = promise.getNow();
            Map<ProxyProviderModel, ServerNodeModel> map = serverConfigModel.getServerConfig();
            for (Map.Entry<ProxyProviderModel, ServerNodeModel> entry : map.entrySet()) {
                persistence.storage(entry.getKey(), entry.getValue());
            }
        } else {
            throw new DaoException(promise.cause());
        }
    }

    private static void loadGatewayConfig(String ip) throws InterruptedException {
        ClusterCenterConnector clusterCenterConnector = ALL_HISTORY_CLUSTER_MAP.get(ip);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new GatewayConfigPullMarkModel());
        Promise<GatewayServiceNodeModel> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
        CenterClusterGatewayConfigResponseMessageHandler.promise = promise;
        clusterCenterConnector.getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send gateway config data error", future.cause());
            }
        });
        if (!promise.await(SYNC_DATA_REQUEST_TIMEOUT, TimeUnit.SECONDS)) {
            log.error("<<<<<<<<<<<<<< Gateway pull service node info timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
        }
        if (promise.isSuccess()) {
            GatewayServiceNodeModel gatewayServiceNodeModel = promise.getNow();
            Map<ProxyProviderModel, GatewayConfigModel> config = gatewayServiceNodeModel.getConfig();
            Map<ProxyProviderModel, Set<ServerNodeModel>> services = gatewayServiceNodeModel.getServices();
            for (Map.Entry<ProxyProviderModel, Set<ServerNodeModel>> entry : services.entrySet()) {
                ProxyProviderModel proxyProviderModel = entry.getKey();
                GatewayModel gatewayModel = new GatewayModel(proxyProviderModel, config.get(proxyProviderModel));
                if (gatewayModel.getGatewayConfigModel() != null) {
                    persistence.storage(gatewayModel);
                }
            }
        } else {
            throw new DaoException(promise.cause());
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
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.INQUIRE_CLUSTER_FULL_CONFIG_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new ConfigMarkModel());
        Promise<FullConfigModel> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
        CenterClusterConfigResponseHandler.promise = promise;
        clusterCenterConnector.getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send full config data error", future.cause());
            }
        });
        if (!promise.await(SYNC_DATA_REQUEST_TIMEOUT, TimeUnit.SECONDS)) {
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

    public static void loadCallTrend(String ip) throws InterruptedException {
        ClusterCenterConnector clusterCenterConnector = ALL_HISTORY_CLUSTER_MAP.get(ip);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.INQUIRE_CLUSTER_FULL_CALL_TREND_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new CallTrendPullMarkModel());
        Promise<CallTrendFullModel> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
        CenterClusterCallTrendResponseHandler.promise = promise;
        clusterCenterConnector.getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send full call trend data error", future.cause());
            }
        });
        if (!promise.await(SYNC_DATA_REQUEST_TIMEOUT, TimeUnit.SECONDS)) {
            log.error("<<<<<<<<<<<<<< get full call trend data timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
        }
        if (promise.isSuccess()) {
            List<CallTrendModel> callTrendModels = promise.getNow().getCallTrendModels();
            for (CallTrendModel callTrendModel : callTrendModels) {
                persistence.callTrendIncrement(callTrendModel);
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
            ServiceShareClusterRequestModel serviceShareClusterRequestModel = new ServiceShareClusterRequestModel();
            serviceShareClusterRequestModel.setType(type);
            serviceShareClusterRequestModel.setRegisterProviderModel(registerProviderModel);
            clusterCenterConnector.share(serviceShareClusterRequestModel);
        }
    }

    /**
     * synchronized config info to cluster
     *
     * @param type
     * @param proxyConfigModel
     * @param configJson
     * @see com.dao.cloud.center.core.handler.SyncClusterInformationRequestHandler
     */
    public static void syncConfigToCluster(byte type, ProxyConfigModel proxyConfigModel, String configJson) {
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            ConfigShareClusterRequestModel configShareClusterRequestModel = new ConfigShareClusterRequestModel();
            configShareClusterRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
            configShareClusterRequestModel.setType(type);
            configShareClusterRequestModel.setProxyConfigModel(proxyConfigModel);
            configShareClusterRequestModel.setConfigJson(configJson);
            DataSyncTask dataSyncTask = new DataSyncTask(clusterCenterConnector, configShareClusterRequestModel);
            SYNC_DATA_THREAD_POOL_EXECUTOR.execute(dataSyncTask);
        }
    }

    /**
     * synchronized gateway config info to cluster
     *
     * @param type
     * @param proxyProviderModel
     * @param gatewayConfigModel
     */
    public static void syncGatewayConfigToCluster(byte type, ProxyProviderModel proxyProviderModel, GatewayConfigModel gatewayConfigModel) {
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            GatewayShareClusterRequestModel gatewayShareClusterRequestModel = new GatewayShareClusterRequestModel();
            gatewayShareClusterRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
            gatewayShareClusterRequestModel.setType(type);
            gatewayShareClusterRequestModel.setProxyProviderModel(proxyProviderModel);
            gatewayShareClusterRequestModel.setGatewayConfigModel(gatewayConfigModel);
            DataSyncTask dataSyncTask = new DataSyncTask(clusterCenterConnector, gatewayShareClusterRequestModel);
            SYNC_DATA_THREAD_POOL_EXECUTOR.execute(dataSyncTask);
        }
    }

    /**
     * synchronized server config info to cluster
     *
     * @param type
     * @param proxyProviderModel
     * @param serverNodeModel
     */
    public static void syncServerConfigToCluster(byte type, ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            ServerShareClusterRequestModel serverShareClusterRequestModel = new ServerShareClusterRequestModel();
            serverShareClusterRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
            serverShareClusterRequestModel.setType(type);
            serverShareClusterRequestModel.setProxyProviderModel(proxyProviderModel);
            serverShareClusterRequestModel.setServerNodeModel(serverNodeModel);
            DataSyncTask dataSyncTask = new DataSyncTask(clusterCenterConnector, serverShareClusterRequestModel);
            SYNC_DATA_THREAD_POOL_EXECUTOR.execute(dataSyncTask);
        }
    }

    /**
     * synchronized call trend info to cluster
     *
     * @param type
     * @param callTrendModel
     */
    public static void syncCallTrendToCluster(byte type, CallTrendModel callTrendModel) {
        for (Map.Entry<String, ClusterCenterConnector> entry : ALL_HISTORY_CLUSTER_MAP.entrySet()) {
            ClusterCenterConnector clusterCenterConnector = entry.getValue();
            CallTrendShareClusterRequestModel callTrendShareClusterRequestModel = new CallTrendShareClusterRequestModel();
            callTrendShareClusterRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
            callTrendShareClusterRequestModel.setType(type);
            callTrendShareClusterRequestModel.setCallTrendModel(callTrendModel);
            DataSyncTask dataSyncTask = new DataSyncTask(clusterCenterConnector, callTrendShareClusterRequestModel);
            SYNC_DATA_THREAD_POOL_EXECUTOR.execute(dataSyncTask);
        }
    }

    /**
     * join cluster
     * This is an idempotent behavior
     *
     * @param ip
     * @param flag 这个标识是否就绪服务。如果就绪，则会发送心跳，否则不发送心跳.
     */
    public static void joinCluster(String ip, boolean flag) {
        log.info("add a new or heartbeat (ip = {}) node cluster", ip);
        if (ALL_HISTORY_CLUSTER_MAP.get(ip) == null) {
            ALL_HISTORY_CLUSTER_MAP.put(ip, new ClusterCenterConnector(ip, flag));
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
        log.info("Inquiring about the cluster node......");
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(inquireIpAddress, DaoCloudConstant.CENTER_PORT);
        bootstrap.group(group);
        // 设置连接超时时间
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ProtocolFrameDecoder()).addLast(new DaoMessageCoder()).addLast(new InquireClusterCenterResponseHandler());
            }
        });
        Channel channel = bootstrap.connect().sync().channel();
        ClusterInquireMarkModel clusterInquireMarkModel = new ClusterInquireMarkModel();
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterInquireMarkModel);
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

    /**
     * Open all cluster heartbeats
     */
    public static void ready() {
        ALL_HISTORY_CLUSTER_MAP.forEach((ip, clusterCenterConnector) -> {
            clusterCenterConnector.ready();
        });
    }
}
