package com.dao.cloud.starter.handler;

import com.dao.cloud.starter.manager.ClientManager;
import com.dao.cloud.starter.unit.Client;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.RpcResponseModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.protocol.HeartbeatPacket;
import com.dao.cloud.core.util.LongPromiseBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description: rpc-client handler
 * 关于服务端与客户端的心跳方案
 * 首先有一个问题? 怎么样感知判断心跳失败(客户端如何得知请求失败?), 心跳所有的设计来自这个问题
 * 应当是以客户端接收到事实的失败响应为判断依据
 * 所以该设计是由客户端侧通过IdleStateHandler读超时的特性设计发送心跳, 注意这里就是维持的心跳启始点
 * 客户端(读超时事件,记录{@link Client.failMark++}) ------发送心跳包------> 服务端(写超时事件)
 * 客户端(移除{@link Client.failMark}=0) <------回应心跳包------ 服务端(写超时事件)
 * 具体思路:
 * 客户端: 设计一个超时事件任务, 这里设计的读事件超时, 在超时中发送心跳包的定时任务
 * bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
 * @Override protected void initChannel(NioSocketChannel ch) throws Exception {
 * ch.pipeline().addLast("clientIdleHandler", new IdleStateHandler(2, 0, 0));
 * }
 * });
 * 服务端: 设计一个超时事件任务, 就是监听读、写事件. 一种是监听客户端发送的心跳读入事件, 一种回心跳包写出去到客户端
 * bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
 * @Override protected void initChannel(NioSocketChannel ch) throws Exception {
 * ch.pipeline().addLast("serverIdleHandler",new IdleStateHandler(0, 0, 8));
 * }
 * }
 * 由此得到如果客户端一直发送心跳包到服务端, 每次发送心跳会记一次{@link Client.failMark}中的标识, 心跳正常是否就看{@link Client.failMark}是否被重置, 在客户端侧会得到客户端回包, 没有被若服务端没有正常回包, {@link Client.failMark}到3次后会自动重试
 */
@Slf4j
public class RpcClientMessageHandler extends SimpleChannelInboundHandler<RpcResponseModel> {

    /**
     * 客户端对象信息
     */
    private Client client;

    public RpcClientMessageHandler(Client client) {
        this.client = client;
    }

    /**
     * 接收rpc 调用返回结果
     *
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param responseModel the response message to handle
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseModel responseModel) {
        // get promise
        Promise<Object> promise = LongPromiseBuffer.getInstance().remove(responseModel.getSequenceId());
        if (promise != null) {
            DaoException daoException = responseModel.getDaoException();
            if (daoException == null) {
                promise.setSuccess(responseModel.getReturnValue());
            } else {
                promise.setFailure(daoException);
            }
        }
    }

    /**
     * 服务端远程关闭连接触发事件
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ClientManager.remove(new ServerNodeModel(client.getIp(), client.getPort()));
        log.error(">>>>>>>>>>> server (connect address = {}) down <<<<<<<<<<<", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    /**
     * 发送心跳包到服务端触发事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            int failMark = client.getFailMark();
            if (failMark >= 3) {
                ClientManager.remove(new ServerNodeModel(client.getIp(), client.getPort()));
                log.error("<<<<<<<<<<< server (connect address = {}) down >>>>>>>>>>>", ctx.channel().remoteAddress());
            } else {
                // send heartbeat packet
                sendHeartBeat(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 发送心跳包
     *
     * @param ctx
     */
    public void sendHeartBeat(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        HeartbeatPacket heartbeatPacket = new HeartbeatPacket();
        channel.writeAndFlush(heartbeatPacket).addListener(future -> {
            if (!future.isSuccess()) {
                client.reconnect();
            }
            client.addFailMark();
        });
    }
}