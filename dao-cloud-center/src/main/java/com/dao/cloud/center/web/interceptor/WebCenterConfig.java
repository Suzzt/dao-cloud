package com.dao.cloud.center.web.interceptor;

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/07/29 17:58
 * web center configuration
 */
@Configuration
public class WebCenterConfig implements WebMvcConfigurer {

    @Resource
    private PermissionInterceptor permissionInterceptor;

    @Resource
    private CookieInterceptor cookieInterceptor;

    private final FreeMarkerProperties freeMarkerProperties;

    public WebCenterConfig(FreeMarkerProperties freeMarkerProperties) {
        this.freeMarkerProperties = freeMarkerProperties;
    }

    @Bean
    @Primary
    public FreeMarkerProperties freeMarkerProperties() {
        freeMarkerProperties.setCharset(Charset.forName("UTF-8"));
        freeMarkerProperties.setRequestContextAttribute("request");
        freeMarkerProperties.getSettings().put("number_format", "0.##########");
        freeMarkerProperties.setSuffix(".ftl");
        freeMarkerProperties.setTemplateLoaderPath("classpath:/templates/");
        return freeMarkerProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}