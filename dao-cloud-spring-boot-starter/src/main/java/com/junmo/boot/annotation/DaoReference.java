package com.junmo.boot.annotation;

import com.junmo.boot.banlance.LoadBalance;
import lombok.NonNull;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2023/1/11 12:25
 * @description: reference service
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DaoReference {

    String proxy();

    LoadBalance loadBalance() default LoadBalance.RANDOM;

    String version() default "";

    long timeout() default 2000;
}
