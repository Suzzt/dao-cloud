package com.dao.cloud.starter.utils;

import java.lang.reflect.Method;

/**
 * @author sucf
 * @date 2025/3/6 12:59
 * @since 1.0.0
 */
public class MethodUtils {
    public static String methodToString(Method method) {
        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        return methodToString(name, parameterTypes);
    }

    public static String methodToString(String methodName, Class<?>[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return methodName;
        }
        StringBuilder params = new StringBuilder();
        for (Class<?> parameterType : parameterTypes) {
            params.append(parameterType.getName()).append(",");
        }
        params = new StringBuilder(params.substring(0, params.length() - 1));
        return String.format("%s(%s)", methodName, params);
    }
}
