package com.dao.cloud.starter.annotation;

import com.dao.cloud.starter.banlance.LoadBalance;
import com.dao.cloud.core.enums.Serializer;

import java.lang.annotation.*;

/**
 * @author sucf
 * @since 1.0
 * reference service
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DaoReference {

    String proxy();

    String provider() default "";

    int version() default 0;

    long timeout() default 2000;

    LoadBalance loadBalance() default LoadBalance.RANDOM;

    Serializer serializable() default Serializer.HESSIAN;
}
