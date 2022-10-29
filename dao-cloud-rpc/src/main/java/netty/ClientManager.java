package netty;

import handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.DefaultMessageCoder;
import protocol.ProtocolFrameDecoder;

/**
 * @author: sucf
 * @date: 2022/10/28 19:04
 * @description:
 */
@Slf4j
public class ClientManager {
    private static volatile Channel channel;
    private static final Object LOCK = new Object();

    /**
     * 获取channel
     *
     * @return
     */
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        DefaultMessageCoder defaultMessageCoder = new DefaultMessageCoder();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(LOGGING_HANDLER)
                        .addLast(defaultMessageCoder)
                        .addLast(rpcResponseMessageHandler);
                System.out.println(">>>>>>>>>>建立连接<<<<<<<<<<<<");
            }
        });
        try {
            channel = bootstrap.connect("localhost", 6661).sync().channel();
            //试试这种listen来关闭
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client exception error", e);
        }
    }
}
