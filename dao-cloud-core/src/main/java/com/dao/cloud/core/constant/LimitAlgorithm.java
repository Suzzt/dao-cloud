package com.dao.cloud.core.constant;

/**
 * @author sucf
 * @date 2025/7/24 23:27:33
 * @since 1.0.0
 */
public class LimitAlgorithm {
    public static final int SLIDE_WINDOW_COUNT = 1;
    public static final int TOKEN_BUCKET = 2;
    public static final int LEAKY_BUCKET = 3;

    private LimitAlgorithm() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
