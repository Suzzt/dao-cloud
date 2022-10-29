package netty;

import handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.DefaultMessageCoder;
import protocol.ProtocolFrameDecoder;

/**
 * @author: sucf
 * @date: 2022/10/28 19:05
 * @description:
 */
@Slf4j
public class ServerManager {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        DefaultMessageCoder defaultMessageCoder = new DefaultMessageCoder();
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(defaultMessageCoder);
                    ch.pipeline().addLast(rpcRequestMessageHandler);
                }
            });
            Channel channel = serverBootstrap.bind(6661).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server interrupted error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
