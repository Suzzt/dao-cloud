package com.junmo.boot.annotation;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2022/11/24 23:25
 * @description: 扫描类接口
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DaoService {
    /**
     * dao proxy name
     * 来确定服务的唯一性
     * @return
     */
    String proxy();
}
