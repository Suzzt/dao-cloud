package com.dao.cloud.starter.utils;

import com.dao.cloud.starter.annotation.HashKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Hash key extractor from method parameters using @HashKey annotation
 * 
 * @author sucf
 * @since 1.0.0
 * @date 2024/1/1 00:00
 */
@Slf4j
public class HashKeyExtractor {
    
    /**
     * Extract hash key from method arguments using @HashKey annotation
     *
     * @param method the method being invoked
     * @param args method arguments
     * @return extracted hash key value, null if no @HashKey found
     */
    public static Object extractHashKey(Method method, Object[] args) {
        if (method == null || args == null || args.length == 0) {
            return null;
        }

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length && i < args.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof HashKey) {
                    HashKey hashKey = (HashKey) annotation;
                    return extractValue(args[i], hashKey.value());
                }
            }
        }

        return null;
    }

    /**
     * Extract value from object using field path
     *
     * @param obj the object to extract from
     * @param fieldPath field path (e.g., "", "userId", "user.id")
     * @return extracted value
     */
    private static Object extractValue(Object obj, String fieldPath) {
        if (obj == null) {
            return null;
        }

        // If no field path specified, use the object itself
        if (!StringUtils.hasLength(fieldPath)) {
            return obj;
        }

        try {
            Object current = obj;
            String[] fields = fieldPath.split("\\.");

            for (String fieldName : fields) {
                if (current == null) {
                    break;
                }

                Field field = current.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                current = field.get(current);
            }

            return current;
        } catch (Exception e) {
            log.warn("Failed to extract hash key from field path: {}, fallback to object itself",
                    fieldPath, e);
            return obj;
        }
    }
}