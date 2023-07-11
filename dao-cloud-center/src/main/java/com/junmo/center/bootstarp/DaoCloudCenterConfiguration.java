package com.junmo.center.bootstarp;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.handler.*;
import com.junmo.center.web.CenterController;
import com.junmo.core.expand.Persistence;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.NetUtil;
import com.junmo.core.util.ThreadPoolFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2022/11/13 23:14
 * @description: register center configuration
 */
@Slf4j
@ComponentScan(value = "com.junmo.center.core.storage")
@Import(ConfigCenterManager.class)
public class DaoCloudCenterConfiguration implements ApplicationListener<ApplicationEvent> {

    @Resource
    private ConfigCenterManager configCenterManager;

    @Resource
    private Persistence persistence;

    /**
     * default hessian serialize
     */
    public static byte SERIALIZE_TYPE = 0;

    @Value(value = "${server.servlet.context-path:#{null}}")
    private String contextPath;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            // init center cluster attribute persistence
            CenterClusterManager.setPersistence(persistence);
            ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
                NioEventLoopGroup boss = new NioEventLoopGroup();
                NioEventLoopGroup worker = new NioEventLoopGroup(4);
                try {
                    ServerBootstrap serverBootstrap = new ServerBootstrap();
                    serverBootstrap.channel(NioServerSocketChannel.class);
                    serverBootstrap.group(boss, worker);
                    serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(new DaoMessageCoder());
                            ch.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new IdleStateHandler(8, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new InquireClusterCenterRequestHandler());
                            ch.pipeline().addLast(new AcceptHeartbeatClusterCenterHandler());
                            ch.pipeline().addLast(new SubscribeConfigHandler(configCenterManager));
                            ch.pipeline().addLast(new PullServerHandler());
                            ch.pipeline().addLast(new PullConfigRequestHandler(configCenterManager));
                            ch.pipeline().addLast(new SyncClusterInformationRequestHandler(configCenterManager));
                            ch.pipeline().addLast(new SyncClusterInformationResponseHandler());
                            ch.pipeline().addLast(new ServerRegisterHandler());
                        }
                    });
                    Channel channel = serverBootstrap.bind(DaoCloudConstant.CENTER_IP).sync().channel();
                    if (StringUtils.hasLength(DaoCloudClusterCenterProperties.ip)) {
                        // join cluster
                        CenterClusterManager.inquireIpAddress = DaoCloudClusterCenterProperties.ip;
                        CenterClusterManager.start();
                    }
                    configCenterManager.init();
                    log.info(">>>>>>>>>>>> dao-cloud-center port:{} start success <<<<<<<<<<<", DaoCloudConstant.CENTER_IP);
                    channel.closeFuture().sync();
                } catch (Exception e) {
                    log.error("dao-cloud center start error", e);
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                    System.exit(1);
                }
            });
        } else if (applicationEvent instanceof WebServerInitializedEvent) {
            WebServerInitializedEvent event = (WebServerInitializedEvent) applicationEvent;
            if (contextPath == null) {
                log.info("======================================== open web dao-cloud page address: http://{}:{}/dao-cloud/index.html ========================================", NetUtil.getLocalIp(), event.getWebServer().getPort());
            } else {
                log.info("======================================== open web dao-cloud page address: http://{}:{}{}/dao-cloud/index.html ========================================", NetUtil.getLocalIp(), event.getWebServer().getPort(), contextPath);
            }
        }
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "dao-cloud.center.dashboard", name = "enabled", matchIfMissing = true)
    public CenterController centerController() {
        return new CenterController();
    }
}
