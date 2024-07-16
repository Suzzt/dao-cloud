package com.dao.cloud.starter.unit;

import com.dao.cloud.core.model.CallTrendModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.DaoTimer;
import com.dao.cloud.starter.manager.CenterChannelManager;
import io.netty.channel.Channel;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: sucf
 * @date: 2024/7/14 23:47
 * @description:
 */
@Slf4j
public class CallTrendTimerTask implements TimerTask {

    private AtomicLong count;

    private ProxyProviderModel proxyProviderModel;

    private String methodName;

    private int interval;

    private TimeUnit timeUnit;

    public CallTrendTimerTask(AtomicLong count, ProxyProviderModel proxyProviderModel, String methodName, int interval, TimeUnit timeUnit) {
        this.count = count;
        this.proxyProviderModel = proxyProviderModel;
        this.methodName = methodName;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * todo 这里可以借鉴concurrentHashmap size的思想做分层相加
     */
    public void increment() {
        count.incrementAndGet();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        try {
            CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, methodName, count.get());
            DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.CALL_TREND_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, callTrendModel);
            Channel channel = CenterChannelManager.getChannel();
            channel.writeAndFlush(daoMessage).addListener(future -> {
                if (future.isSuccess()) {
                    count.set(0);
                } else {
                    log.error("<<<<<<<<< send call data error >>>>>>>>>", future.cause());
                }
            });
        } catch (Exception e) {
            log.error("<<<<<<<<<<< sync call trend error >>>>>>>>>>>", e);
        } finally {
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, interval, timeUnit);
        }
    }
}
