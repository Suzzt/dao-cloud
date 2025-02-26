package com.dao.cloud.starter.annotation;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author sucf
 * @since 1.0
 */
public class UseOnAnnotationCondition implements ConfigurationCondition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Map<String, Object> map = annotatedTypeMetadata.getAnnotationAttributes(ConditionalOnUseAnnotation.class.getName());
        Map<String, Object> serviceBeanMap = conditionContext.getBeanFactory().getBeansWithAnnotation((Class<? extends Annotation>) map.get("annotation"));
        return !CollectionUtils.isEmpty(serviceBeanMap);
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}
