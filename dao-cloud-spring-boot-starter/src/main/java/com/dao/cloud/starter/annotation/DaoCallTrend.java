package com.dao.cloud.starter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2024/07/10 23:55
 * @description: Interface call trend statistics.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DaoCallTrend {

    /**
     * Statistics interval number
     *
     * @return
     */
    int interval() default 1;

    /**
     * Statistics interval unit
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.HOURS;
}
