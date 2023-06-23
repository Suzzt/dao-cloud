package com.junmo.boot.bootstrap;

import com.junmo.boot.bootstrap.manager.CenterChannelManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2023/6/10 12:39
 * @description:
 */
@Component
public class DaoCloudApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        // init the connection cluster
        CenterChannelManager.init(args.getOptionValues("dao-cloud.center.ip").get(0));
    }
}
