package com.junmo.config.register;

import com.junmo.config.register.handler.PollServerHandler;
import com.junmo.config.register.handler.ServerRegisterMessageHandler;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.netty.serialize.SerializeStrategyFactory;
import com.junmo.core.util.ThreadPoolFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2022/11/13 23:14
 * @description: 配置中心
 */
@Component
@Slf4j
public class RegisterConfig implements ApplicationContextAware {

    public static byte SERIALIZE_TYPE;

    private final int port = 5551;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String serializer = applicationContext.getEnvironment().getProperty("dao-cloud.serializer");
        SERIALIZE_TYPE = SerializeStrategyFactory.getSerializeType(serializer);
        ThreadPoolFactory.GLOBAL_THREAD_POOL.submit(() -> {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup(4);
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.group(boss, worker);
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(new DaoMessageCoder());
                        ch.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new PollServerHandler());
                        ch.pipeline().addLast(new ServerRegisterMessageHandler());
                    }
                });
                Channel channel = serverBootstrap.bind(port).sync().channel();
                log.info(">>>>>>>>>>>> dao-center start success <<<<<<<<<<<");
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("server interrupted error", e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        });
    }
}
