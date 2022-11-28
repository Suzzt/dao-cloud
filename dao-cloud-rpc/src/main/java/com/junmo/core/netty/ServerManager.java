package com.junmo.core.netty;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2022/10/28 19:05
 * @description:
 */
@Slf4j
public class ServerManager {

    public static void startup() {
        ThreadUtil.execute(new ServerNetty());
    }
}
