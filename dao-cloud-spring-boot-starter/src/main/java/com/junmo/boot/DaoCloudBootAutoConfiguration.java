package com.junmo.boot;

import com.junmo.boot.registry.ServerRegistry;
import com.junmo.core.netty.ServerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2022/10/28 20:29
 * @description: SpringBoot Auto Configuration
 */
@Configuration
@Component
public class DaoCloudBootAutoConfiguration {
    @Bean
    public void init() throws Exception {
        //启动自己的服务处理端
        ServerManager.startup();

        //把自己注册到配置中心(ServerRegisterManager),建立心跳连接to配置中心
        ServerRegistry.registry();

        System.err.println("======rpc startup over=====");
    }

}
