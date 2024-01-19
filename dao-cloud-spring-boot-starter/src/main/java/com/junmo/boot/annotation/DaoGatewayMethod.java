package com.junmo.boot.annotation;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2024/1/19 11:25
 * @description: 网关方法决定定制
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DaoGatewayMethod {

    /**
     * 以gateway admin web ui为最终标准
     */
    String mapping();

    /**
     * 限流类型
     */
    String limit();

}
