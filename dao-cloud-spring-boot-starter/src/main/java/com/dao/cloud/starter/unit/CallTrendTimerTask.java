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
import java.util.concurrent.atomic.LongAdder;

/**
 * @author: sucf
 * @date: 2024/7/14 23:47
 * @description: 缓冲分段计数思路
 * 具体思路如下：
 * 使用 LongAdder 提高 increment 性能：LongAdder 能在高并发下有效减少竞争，比 AtomicLong 更适合频繁增量操作。
 * 发送失败后的计数恢复：在 backupBuffer 中保存一次性总计数，如果发送失败，将 backupBuffer 的值重新加回 activeBuffer，确保计数不丢失。
 * 更灵活的双缓冲机制：每次 run 方法调用时，activeBuffer 清零，直接复用它继续计数，避免额外的切换开销。
 */
@Slf4j
public class CallTrendTimerTask implements TimerTask {

    /**
     * 主计数缓冲区
     */
    private final LongAdder activeBuffer = new LongAdder();
    /**
     * 备份缓冲区
     */
    private final AtomicLong backupBuffer = new AtomicLong(0);

    private final ProxyProviderModel proxyProviderModel;
    private final String methodName;
    private final int interval;
    private final TimeUnit timeUnit;

    public CallTrendTimerTask(ProxyProviderModel proxyProviderModel, String methodName, int interval, TimeUnit timeUnit) {
        this.proxyProviderModel = proxyProviderModel;
        this.methodName = methodName;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * 高并发计数方法，计数操作只作用于 activeBuffer
     */
    public void increment() {
        activeBuffer.increment();
    }

    @Override
    public void run(Timeout timeout) {
        try {
            // 获取并交换缓冲区计数
            long totalCount = activeBuffer.sumThenReset();
            if (totalCount != 0) {
                // 将统计值转移到备份缓冲区，用于回退
                backupBuffer.set(totalCount);

                CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, methodName, totalCount);
                DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.CALL_TREND_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, callTrendModel);
                Channel channel = CenterChannelManager.getChannel();

                channel.writeAndFlush(daoMessage).addListener(future -> {
                    if (future.isSuccess()) {
                        backupBuffer.set(0);
                    } else {
                        activeBuffer.add(backupBuffer.get());
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