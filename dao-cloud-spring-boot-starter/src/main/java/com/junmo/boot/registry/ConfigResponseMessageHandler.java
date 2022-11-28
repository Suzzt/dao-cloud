package com.junmo.boot.registry;

import com.junmo.core.model.PingPong;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2022/11/19 09:11
 * @description:
 */
@Slf4j
public class ConfigResponseMessageHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            //heart
            if (!msg.toString().equals(PingPong.ping())) {
                log.error("=======xxxxxxxx heart beat disconnection xxxxxxxx=======");
                return;
            }
            ServerRegistry.HEART_BEAT = true;
            log.debug("======✓✓✓✓✓✓heart beat dao bingo✓✓✓✓✓✓======");
        } else {
            //register
            log.debug("{}", msg);
        }

    }
}