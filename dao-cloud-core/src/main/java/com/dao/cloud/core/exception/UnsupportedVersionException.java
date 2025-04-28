package com.dao.cloud.core.exception;

/**
 * dao协议版本异常
 *
 * @author sucf
 * @date 2025/4/28 16:26
 * @since 1.0.0
 */
public class UnsupportedVersionException extends RuntimeException {
    public UnsupportedVersionException(String message) {
        super(message);
    }
}
