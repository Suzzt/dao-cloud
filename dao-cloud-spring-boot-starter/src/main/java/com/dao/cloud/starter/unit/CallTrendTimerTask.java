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
import java.util.concurrent.atomic.AtomicBoolean;
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

    private final AtomicBoolean concurrentCtrl = new AtomicBoolean(false);

    /**
     * 主计数缓冲区
     */
    private final LongAdder activeBuffer = new LongAdder();
    /**
     * 备份缓冲区
     */
    private long lastTotalCount = 0L;

    private final AtomicLong failCountBuffer = new AtomicLong(0L);

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
        if (concurrentCtrl.compareAndSet(false, true)) {
            try {
                // 保证线程安全，避免因为各种原因导致的定时任务与下一个周期碰撞上（理论上不会碰上的，不过还是加下）

                // 获取并交换缓冲区计数
                long allTotalCount = activeBuffer.sum();
                long deltaCount = allTotalCount - lastTotalCount;
                long failCount = failCountBuffer.get();
                if (failCount > 0L) {
                    failCountBuffer.getAndAdd(-failCount);
                }
                long totalCount = deltaCount + failCount;
                if (totalCount != 0) {
                    lastTotalCount += deltaCount;
                    CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, methodName, totalCount);
                    DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.CALL_TREND_RESPONSE_MESSAGE,
                        DaoCloudConstant.DEFAULT_SERIALIZE, callTrendModel);
                    Channel channel = CenterChannelManager.getChannel();

                    channel.writeAndFlush(daoMessage).addListener(future -> {
                        if (!future.isSuccess()) {
                            failCountBuffer.getAndAdd(totalCount);
                            log.error("<<<<<<<<< send call data error >>>>>>>>>", future.cause());
                        }
                    });
                }
            } catch (Exception e) {
                log.error("<<<<<<<<<<< sync call trend error >>>>>>>>>>>", e);
            } finally {
                concurrentCtrl.set(false);
                DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, interval, timeUnit);
            }
        }
    }
}