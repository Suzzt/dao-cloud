package com.junmo.boot.registry;

import com.junmo.boot.channel.ConfigChannelManager;
import com.junmo.core.model.DaoMessage;
import io.netty.channel.Channel;


/**
 * @author: sucf
 * @date: 2022/11/26 23:43
 * @description:
 */
public class PingPongThread implements Runnable {

    @Override
    public void run() {
        while (true){
            Channel channel = ConfigChannelManager.getChannel();
            DaoMessage heartbeatMessage = new DaoMessage();
            channel.writeAndFlush(heartbeatMessage);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
