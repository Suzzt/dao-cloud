package com.dao.cloud.starter.annotation;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author sucf
 * @since 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({UseOnAnnotationCondition.class})
public @interface ConditionalOnUseAnnotation {
     Class<?> annotation();
}
