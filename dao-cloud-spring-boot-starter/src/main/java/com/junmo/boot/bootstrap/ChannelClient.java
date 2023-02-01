package com.junmo.boot.bootstrap;

import com.junmo.boot.handler.RpcClientMessageHandler;
import com.junmo.core.exception.DaoException;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
public class ChannelClient {

    private NioEventLoopGroup group;

    private final Object lock = new Object();

    private String proxy;

    private String ip;

    private int port;

    /**
     * connect channel
     */
    private Channel channel;

    /**
     * fail mark count
     * if >3. it will be eliminated
     */
    private int failMark = 0;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelClient that = (ChannelClient) o;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    public ChannelClient(String proxy, String ip, int port) {
        this.proxy = proxy;
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

    public void reconnect() {
        channel.close().addListener(future -> {
            channel.eventLoop().schedule(() -> connect(), 5, TimeUnit.SECONDS);
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
        RpcClientMessageHandler rpcClientMessageHandler = new RpcClientMessageHandler(proxy, this);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast("clientIdleHandler", new IdleStateHandler(2, 0, 0, TimeUnit.SECONDS))
                        .addLast(rpcClientMessageHandler);
                log.info(">>>>>>>>>> dao-cloud-rpc connect server (ip = {},port = {}) success <<<<<<<<<<<<", ip, port);
            }
        });
        try {
            this.channel = bootstrap.connect(this.ip, this.port).sync().channel();
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
