package com.dao.cloud.starter.annotation;

import com.dao.cloud.core.enums.Serializer;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/11/24 23:25
 * provider exposure service
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
