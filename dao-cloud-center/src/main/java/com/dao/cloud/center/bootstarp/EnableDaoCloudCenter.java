package com.dao.cloud.center.bootstarp;

import com.dao.cloud.center.properties.DaoCloudClusterCenterProperties;
import com.dao.cloud.center.properties.DaoCloudConfigCenterProperties;
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
@EnableConfigurationProperties({DaoCloudConfigCenterProperties.class, DaoCloudClusterCenterProperties.class})
@Import({DaoCloudCenterConfiguration.class})
public @interface EnableDaoCloudCenter {
}
