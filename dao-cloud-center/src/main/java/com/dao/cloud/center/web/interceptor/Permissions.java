package com.dao.cloud.center.web.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: sucf
 * @date: 2023/07/29 17:22
 * @description: 定义一个简单注解, 用来拦截用户登录状态
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    /**
     * 登陆拦截 (默认拦截)
     */
    boolean limit() default true;

}