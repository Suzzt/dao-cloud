package com.junmo.boot;

import com.junmo.boot.channel.ChannelManager;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.boot.registry.ClientManager;
import com.junmo.boot.registry.ServerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: sucf
 * @date: 2022/10/28 20:29
 * @description: rpc SpringBoot Auto Configuration
 */
@Configuration
@EnableConfigurationProperties(DaoCloudProperties.class)
@ConditionalOnProperty(prefix = "dao-cloud", name = "enable", havingValue = "true")
@Slf4j
public class DaoCloudConfiguration {

    @Bean
    public ServerManager start() {
        ServerManager serverManager = new ServerManager();
        log.info(">>>>>>>>>>> dao-cloud-rpc server manager config init finish. <<<<<<<<<< :)bingo(:");
        return serverManager;
    }

    @Bean
    public ClientManager initClient() {
        ClientManager clientManager = new ClientManager();
        log.info(">>>>>>>>>>> dao-cloud-rpc client manager config init finish. <<<<<<<<<< :)bingo(:");
        return clientManager;
    }

}
