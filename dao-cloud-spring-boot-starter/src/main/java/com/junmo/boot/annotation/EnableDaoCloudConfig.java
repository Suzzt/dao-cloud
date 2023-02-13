package com.junmo.boot.annotation;

import com.junmo.boot.bootstrap.DaoCloudConfigConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2023/2/10 23:43
 * @description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DaoCloudConfigConfiguration.class)
public @interface EnableDaoCloudConfig {
    String[] proxy() ;
}
