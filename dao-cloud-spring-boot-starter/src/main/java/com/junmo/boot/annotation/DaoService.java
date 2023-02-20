package com.junmo.boot.annotation;

import com.junmo.core.enums.Serializer;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author: sucf
 * @date: 2022/11/24 23:25
 * @description: provider service
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface DaoService {
    String provider() default "";

    int version() default 0;

    Serializer serializable() default Serializer.HESSIAN;
}
