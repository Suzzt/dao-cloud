package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.starter.properties.DaoCloudConfigurationProperties;
import com.dao.cloud.starter.properties.DaoCloudPropertySourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: sucf
 * @date: 2023/2/12 16:37
 * @description: Configuration center startup
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({DaoCloudPropertySourceProperties.class, DaoCloudConfigurationProperties.class})
public class ConfigurationCenterBootstrap {

}
