package com.dao.cloud.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sucf
 * @date 2023/2/2 22:03
 * @since 1.0.0
 */
public class GsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonUtils.class);

    private static final Gson GSON = new Gson();

    private GsonUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    /**
     * 对象转JSON字符串
     *
     * @param obj 要序列化的对象
     * @return JSON字符串，对象为null时返回"null"
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * JSON字符串转对象（支持泛型类）
     *
     * @param json  JSON字符串
     * @param clazz 目标类
     * @param <T>   目标类型
     * @return 解析后的对象，解析失败返回null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            LOGGER.error("JSON字符串转对象失败：{}", e.getMessage());
            throw e;
        }
    }
}