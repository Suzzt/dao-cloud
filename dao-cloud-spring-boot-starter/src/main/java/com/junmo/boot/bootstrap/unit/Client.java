package com.junmo.boot.bootstrap.unit;

import com.junmo.boot.handler.ClientPingPongMessageHandler;
import com.junmo.boot.handler.RpcClientMessageHandler;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/1/15 15:03
 * @description:
 */
@Data
@Slf4j
public class Client {

    /**
     * 锁对象. 单例加载channel
     */
    private final Object lock = new Object();

    private ProxyProviderModel proxyProviderModel;

    /**
     * rpc provider ip
     */
    private String ip;

    /**
     * rpc provider port
     */
    private int port;

    /**
     * 长连接可操作的channel
     */
    private volatile Channel channel;

    /**
     * 客户端Bootstrap
     */
    private Bootstrap bootstrap = new Bootstrap();

    /**
     * 客户端NioEventLoopGroup
     */
    private NioEventLoopGroup group;

    /**
     * fail mark count
     * if >3. it will be eliminated
     */
    private volatile int failMark = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client that = (Client) o;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    public Client(ProxyProviderModel proxyProviderModel, String ip, int port) {
        this.proxyProviderModel = proxyProviderModel;
        this.ip = ip;
        this.port = port;
    }

    /**
     * get channel
     *
     * @return
     */
    public Channel getChannel() {
        if (this.channel != null) {
            return this.channel;
        }
        synchronized (lock) {
            if (this.channel != null) {
                return this.channel;
            }
            connect();
            return this.channel;
        }
    }

    /**
     * reconnect
     */
    public void reconnect() {
        channel.close().addListener(future -> {
            channel.eventLoop().schedule(() -> {
                bootstrap.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            channel = future.channel();
                            log.info(">>>>>>>>> reconnect server channel success. <<<<<<<<<< :)bingo(:");
                        } else {
                            log.error("<<<<<<<<<< reconnect server center error >>>>>>>>>>", future.cause());
                        }
                    }
                });
            }, 5, TimeUnit.SECONDS);
        });
    }

    /**
     * destroy
     */
    public void destroy() {
        try {
            this.getChannel().close().sync();
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }

    /**
     * connect server
     */
    private void connect() {
        group = new NioEventLoopGroup();
        RpcClientMessageHandler rpcClientMessageHandler = new RpcClientMessageHandler(proxyProviderModel, this);
        ClientPingPongMessageHandler clientPingPongMessageHandler = new ClientPingPongMessageHandler(this);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(this.ip, this.port);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast("clientIdleHandler", new IdleStateHandler(2, 0, 0, TimeUnit.SECONDS))
                        .addLast(rpcClientMessageHandler)
                        .addLast(clientPingPongMessageHandler);
                log.info(">>>>>>>>>> dao-cloud-rpc connect server (ip = {},port = {}) success <<<<<<<<<<<<", ip, port);
            }
        });
        try {
            this.channel = bootstrap.connect().sync().channel();
        } catch (Exception e) {
            log.error("dao-cloud-rpc connect server (ip = {},port = {}) fair<<<<<<<<<<<<", e);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    public void clearFailMark() {
        failMark = 0;
    }

    public void addFailMark() {
        failMark++;
    }
}
