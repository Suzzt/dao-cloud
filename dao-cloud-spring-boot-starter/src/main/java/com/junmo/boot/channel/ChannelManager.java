package com.junmo.boot.channel;

import com.junmo.boot.handler.ConfigResponseMessageHandler;
import com.junmo.common.util.ThreadPoolFactory;
import com.junmo.core.netty.protocol.DefaultMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2023/1/12 21:18
 * @description:
 */
@Component
@Slf4j
public class ChannelManager implements ApplicationContextAware {
    private Channel registerChannel;

    private Channel serviceChannel;

    private Channel clientChannel;

    /**
     * init channels
     */
    private void init() {
        register();
        service();
    }

    private void service() {


    }


    private void register(){
//        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(()->{
//            NioEventLoopGroup group = new NioEventLoopGroup();
//            LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.channel(NioSocketChannel.class);
//            bootstrap.group(group);
//            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                protected void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline()
//                            .addLast(new ProtocolFrameDecoder())
//                            .addLast(LOGGING_HANDLER)
//                            .addLast(new DefaultMessageCoder())
//                            .addLast(new ConfigResponseMessageHandler());
//                }
//            });
//            try {
//                registerChannel = bootstrap.connect("dao.cloud.config.com", 5551).sync().channel();
//                log.info(">>>>>>>>> connect register channel finish. <<<<<<<<<< :)bingo(:");
//            } catch (Exception e) {
//                group.shutdownGracefully();
//                log.error("connect register center error", e);
//            }
//        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
