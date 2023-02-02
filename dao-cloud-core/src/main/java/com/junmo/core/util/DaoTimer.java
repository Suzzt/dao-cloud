package com.junmo.core.util;

import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/2/2 22:03
 * @description:
 */
@Slf4j
public class DaoTimer {
    public static final HashedWheelTimer HASHED_WHEEL_TIMER = new HashedWheelTimer(
            new DefaultThreadFactory("register-timer"),
            100,
            TimeUnit.MILLISECONDS,
            512,
            true);
}
