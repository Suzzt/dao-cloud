package com.junmo.boot.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2024/1/19 10:52
 * @description: 网关全局控制器
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface DaoGatewayController {
    String limit() default "count";

    int version() default 0;
}
