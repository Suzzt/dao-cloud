package com.junmo.center.bootstarp;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2023/2/10 23:43
 * @description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DaoConfig {
    String name();
}
