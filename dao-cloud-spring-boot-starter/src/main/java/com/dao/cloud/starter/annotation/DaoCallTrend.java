package com.dao.cloud.starter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0
 * Interface call trend statistics.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
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
    TimeUnit time_unit() default TimeUnit.HOURS;
}
