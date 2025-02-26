package com.dao.cloud.gateway.intercept.annotation;


import java.lang.annotation.*;

/**
 * @author sucf
 * @since 1.0
 * 网关拦截器注册
 * 记得一定要把装饰该注解的类注册到spring容器中(确保你的类在spring扫描中)！！！！！！！
 * 你可以加@Component、@Bean、XML等方式来加载到spring容器中.详细可翻阅：@see https://spring.io 查看相关文档
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GatewayInterceptorRegister {
    /**
     * Loading priority
     *
     * @return
     */
    int order() default 0;
}
