package com.junmo.boot.registry;

import cn.hutool.core.thread.ThreadUtil;
import com.junmo.boot.channel.ConfigChannelManager;
import com.junmo.core.enums.Constant;
import com.junmo.core.model.DaoMessage;
import com.junmo.core.model.ServerRegisterModel;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * @author: sucf
 * @date: 2022/11/19 17:53
 * @description:
 */
@Slf4j
public class ServerRegistry {

    public static Boolean HEART_BEAT = false;

    /**
     * 发送注册请求
     *
     * @param serverRegisterModel
     */
    public static void send(ServerRegisterModel serverRegisterModel) throws Exception {
        Channel channel = ConfigChannelManager.getChannel();
        if(channel==null){
            throw new Exception("connect config center error");
        }
        DaoMessage daoMessage = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, serverRegisterModel);
        channel.writeAndFlush(daoMessage);
    }

    /**
     * 服务注册
     *
     * @return
     */
    public static void registry() throws Exception {
        ServerRegisterModel serverRegisterModel = new ServerRegisterModel();
        String hostAddress = "";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverRegisterModel.setIpAddress(hostAddress);
        serverRegisterModel.setProxyName("dao-demo-proxy");
        send(serverRegisterModel);
        //heart

        ThreadUtil.execute(new PingPongThread());
    }
}
