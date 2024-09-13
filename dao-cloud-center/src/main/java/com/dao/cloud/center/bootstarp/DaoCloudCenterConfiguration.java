package com.dao.cloud.center.bootstarp;

import com.dao.cloud.center.core.*;
import com.dao.cloud.center.core.handler.*;
import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.center.properties.DaoCloudClusterCenterProperties;
import com.dao.cloud.center.web.controller.CenterController;
import com.dao.cloud.center.web.controller.IndexController;
import com.dao.cloud.center.web.interceptor.CookieInterceptor;
import com.dao.cloud.center.web.interceptor.PermissionInterceptor;
import com.dao.cloud.center.web.interceptor.WebCenterConfig;
import com.dao.cloud.core.netty.handler.PrintExceptionHandler;
import com.dao.cloud.core.netty.protocol.DaoMessageCoder;
import com.dao.cloud.core.netty.protocol.ProtocolFrameDecoder;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.NetUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
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
@ComponentScan(value = "com.dao.cloud.center.core.storage")
@Import({RegisterCenterManager.class, ConfigCenterManager.class, GatewayCenterManager.class, WebCenterConfig.class, LogManager.class})
public class DaoCloudCenterConfiguration implements ApplicationListener<ApplicationEvent> {

    @Resource
    private ConfigCenterManager configCenterManager;

    @Resource
    private GatewayCenterManager gatewayCenterManager;

    @Resource
    private RegisterCenterManager registerCenterManager;

    @Resource
    private LogManager logManager;

    @Resource
    private Persistence persistence;

    /**
     * default hessian serialize
     */
    public static byte SERIALIZE_TYPE = 0;

    @Value(value = "${server.servlet.context-path:#{null}}")
    private String contextPath;

    private boolean flag = false;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            // init center cluster attribute persistence
            CenterClusterManager.setPersistence(persistence);
            ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
                NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("dao-center-boss", true));
                NioEventLoopGroup worker = new NioEventLoopGroup(4, new DefaultThreadFactory("dao-center-worker", true));
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
                            ch.pipeline().addLast(new InquireClusterCenterRequestHandler());
                            ch.pipeline().addLast(new ClusterRequestHandler());
                            ch.pipeline().addLast(new SubscribeConfigHandler(configCenterManager));
                            ch.pipeline().addLast(new GatewayServiceConfigHandler(registerCenterManager, gatewayCenterManager));
                            ch.pipeline().addLast(new CenterClusterServerConfigRequestHandler(registerCenterManager));
                            ch.pipeline().addLast(new CenterClusterCallTrendRequestHandler(registerCenterManager));
                            ch.pipeline().addLast(new PullServerRequestHandler(registerCenterManager));
                            ch.pipeline().addLast(new PullConfigRequestHandler(configCenterManager));
                            ch.pipeline().addLast(new SyncClusterInformationRequestHandler(registerCenterManager, configCenterManager, gatewayCenterManager));
                            ch.pipeline().addLast(new ServerRegisterHandler(registerCenterManager));
                            ch.pipeline().addLast(new ReceiveCallTrendHandler(registerCenterManager));
                            ch.pipeline().addLast(new LogHandler(logManager));
                            ch.pipeline().addLast(new PrintExceptionHandler());
                        }
                    });
                    serverBootstrap.bind(DaoCloudConstant.CENTER_PORT).sync();
                    if (StringUtils.hasLength(DaoCloudClusterCenterProperties.ip)) {
                        // join cluster
                        CenterClusterManager.inquireIpAddress = DaoCloudClusterCenterProperties.ip;
                        CenterClusterManager.start();
                    }

                    log.info("Load initial configuration data into memory (Waiting......)");
                    // load config to cache
                    configCenterManager.init();
                    // load gateway to cache
                    gatewayCenterManager.init();
                    // load service to cache
                    registerCenterManager.init();
                    log.info("Load initial configuration data into memory (Finish)");
                    // Before the above procedures are executed, the node cannot provide service capabilities.
                    CenterClusterManager.ready();
                    log.info(">>>>>>>>>>>> dao-cloud-center port: {}(tcp) start success <<<<<<<<<<<", DaoCloudConstant.CENTER_PORT);
                } catch (Exception e) {
                    log.error("dao-cloud center start error", e);
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                    System.exit(1);
                }
                flag = true;
            });
        } else if (applicationEvent instanceof WebServerInitializedEvent) {
            ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
                while (!flag) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                WebServerInitializedEvent event = (WebServerInitializedEvent) applicationEvent;
                String border = "================================================================================================";

                String protocol = "http://";
                String ipAddress = NetUtil.getLocalIp();
                int port = event.getWebServer().getPort();
                String portString = Integer.toString(port);
                String resourcePath = (contextPath == null ? "/dao-cloud/index" : contextPath + "/dao-cloud/index");
                String address = protocol + ipAddress + ":" + portString + resourcePath;

                // 开始计算内容行的等号填充
                String preLogLine = "   open dao-cloud center page address: " + address + "   ";
                int totalPadding = border.length() - preLogLine.length();
                int sidePadding = totalPadding / 2;

                String sideEqual = new String(new char[sidePadding]).replace("\0", "=");
                String logLine = sideEqual + preLogLine + sideEqual;

                // 如果字符串长度是奇数，调整填充以保持长度和边框一致
                if (logLine.length() < border.length()) {
                    logLine += "=";
                }

                log.info("\n\n" + border + "\n" + logLine + "\n" + border + "\n");
            });
        }
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "dao-cloud.center.admin-web.dashboard", name = "enabled", matchIfMissing = true)
    public CookieInterceptor cookieInterceptor() {
        return new CookieInterceptor();
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "dao-cloud.center.admin-web.dashboard", name = "enabled", matchIfMissing = true)
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "dao-cloud.center.admin-web.dashboard", name = "enabled", matchIfMissing = true)
    public CenterController centerController(RegisterCenterManager registryCenterManager, ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager, LogManager logManager) {
        return new CenterController(registryCenterManager, configCenterManager, gatewayCenterManager, logManager);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "dao-cloud.center.admin-web.dashboard", name = "enabled", matchIfMissing = true)
    public IndexController indexController(RegisterCenterManager registryCenterManager, ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager) {
        return new IndexController(registryCenterManager, configCenterManager, gatewayCenterManager);
    }
}
