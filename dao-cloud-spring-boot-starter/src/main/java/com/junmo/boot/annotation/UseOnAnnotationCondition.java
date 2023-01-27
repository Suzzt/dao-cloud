package com.junmo.boot.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/1/27 21:44
 * @description:
 */
public class UseOnAnnotationCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Map<String, Object> map = annotatedTypeMetadata.getAnnotationAttributes(ConditionalOnUseAnnotation.class.getName());
        Map<String, Object> serviceBeanMap = conditionContext.getBeanFactory().getBeansWithAnnotation((Class<? extends Annotation>) map.get("annotation"));
        return !CollectionUtils.isEmpty(serviceBeanMap);
    }
}
