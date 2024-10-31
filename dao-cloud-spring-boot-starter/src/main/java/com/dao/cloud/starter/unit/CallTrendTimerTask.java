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

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author: sucf
 * @date: 2024/7/14 23:47
 * @description: 双缓冲分段计数思路
 * 具体思路如下：
 * 1.启动两个计数分段数组：activeBuffer（活跃缓冲）和 backupBuffer（备份缓冲）。
 * 1.1:increment() 操作始终操作 active Buffer，以确保计数操作高效。
 * 1.2:在执行 run()（即发送数据）时，将当前的 buffer1 和 buffer2 交换，然后发送 active Buffer 的数据，再将上一个 active Buffer 清零。
 * 2.交换之后，业务逻辑的 increment() 仍然操作新的 active Buffer，不受影响。
 * 3.发送完数据并重置 backup Buffer 后，下次触发 run() 时就可以直接使用 backup Buffer 来统计上一轮的计数，从而确保计数不丢失。
 */
@Slf4j
public class CallTrendTimerTask implements TimerTask {

    /**
     * 缓冲区1
     */
    private final AtomicLongArray buffer1;
    /**
     * 缓冲区2
     */
    private final AtomicLongArray buffer2;
    /**
     * 标识当前的活跃缓冲区
     */
    private volatile boolean isActive = true;
    private final ProxyProviderModel proxyProviderModel;
    private final String methodName;
    private final int interval;
    private final TimeUnit timeUnit;

    /**
     * 分段数=CPU核数
     */
    private static final int SEGMENT_COUNT = Runtime.getRuntime().availableProcessors();

    public CallTrendTimerTask(ProxyProviderModel proxyProviderModel, String methodName, int interval, TimeUnit timeUnit) {
        this.buffer1 = new AtomicLongArray(SEGMENT_COUNT);
        this.buffer2 = new AtomicLongArray(SEGMENT_COUNT);
        this.proxyProviderModel = proxyProviderModel;
        this.methodName = methodName;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * 计数，随机选择一个分段更新
     */
    public void increment() {
        int segmentIndex = ThreadLocalRandom.current().nextInt(SEGMENT_COUNT);
        if (isActive) {
            buffer1.incrementAndGet(segmentIndex);
        } else {
            buffer2.incrementAndGet(segmentIndex);
        }
    }

    /**
     * 获取计数数组的总和
     */
    private long getTotalCount(AtomicLongArray buffer) {
        long sum = 0;
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            sum += buffer.get(i);
        }
        return sum;
    }

    /**
     * 重置计数数组
     */
    private void resetBuffer(AtomicLongArray buffer) {
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            buffer.set(i, 0);
        }
    }

    @Override
    public void run(Timeout timeout) {
        try {
            // 交换缓冲区
            isActive = !isActive;
            // 选择当前的非活跃缓冲区发送数据
            AtomicLongArray bufferToSend = isActive ? buffer2 : buffer1;
            long totalCount = getTotalCount(bufferToSend);
            if (totalCount != 0) {
                CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, methodName, totalCount);
                DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.CALL_TREND_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, callTrendModel);
                Channel channel = CenterChannelManager.getChannel();

                channel.writeAndFlush(daoMessage).addListener(future -> {
                    if (future.isSuccess()) {
                        resetBuffer(bufferToSend);
                    } else {
                        log.error("<<<<<<<<< send call data error >>>>>>>>>", future.cause());
                    }
                });
            }
        } catch (Exception e) {
            log.error("<<<<<<<<<<< sync call trend error >>>>>>>>>>>", e);
        } finally {
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, interval, timeUnit);
        }
    }
}