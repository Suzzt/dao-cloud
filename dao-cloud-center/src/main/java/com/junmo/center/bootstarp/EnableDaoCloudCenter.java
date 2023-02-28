package com.junmo.center.bootstarp;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * @author: sucf
 * @date: 2023/2/6 19:20
 * @description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({DaoCloudCenterProperties.class})
@Import({DaoCloudCenterProperties.class, DaoCloudCenterConfiguration.class})
public @interface EnableDaoCloudCenter {
}
