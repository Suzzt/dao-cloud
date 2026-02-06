package com.dao.cloud.starter.annotation;

import java.lang.annotation.*;

/**
 * Mark method parameter as hash key for load balancing
 * Only effective when using LoadBalance.HASH
 * 
 * @author sucf
 * @since 1.0.0
 * @date 2024/1/1 00:00
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HashKey {
    
    /**
     * Field path to extract hash key from parameter object
     * Examples:
     * - "" (empty): use the parameter itself as hash key
     * - "userId": use the userId field of the parameter
     * - "user.id": use nested field access
     */
    String value() default "";
}