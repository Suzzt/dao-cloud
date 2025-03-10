package com.dao.cloud.starter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/07/10 23:55
 * Interface call trend statistics.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DaoCallTrend {

    /**
     * Statistics interval number
     */
    int interval() default 1;

    /**
     * Statistics interval unit
     */
    TimeUnit time_unit() default TimeUnit.HOURS;
}
