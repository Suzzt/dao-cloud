package com.dao.cloud.starter.annotation;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2023/1/27 21:43
 * @description:
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({UseOnAnnotationCondition.class})
public @interface ConditionalOnUseAnnotation {
     Class<?> annotation();
}
