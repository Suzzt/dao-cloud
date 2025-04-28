package com.dao.cloud.core.netty.protocol;

import com.dao.cloud.core.enums.Serializer;
import com.dao.cloud.core.util.DaoCloudConstant;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/10/29 19:49
 * 消息结构载体
 */
@Data
public class DaoMessage<T> {
    public DaoMessage(byte version, byte messageType, byte serializableType, T content) {
        this.version = version;
        this.messageType = messageType;
        this.serializableType = serializableType;
        this.content = content;
    }

    public DaoMessage() {
    }

    /**
     * 魔数
     * 3byte
     */
    private byte[] magicNumber = DaoCloudConstant.MAGIC_NUMBER;

    /**
     * 消息类型
     * 1byte
     */
    private byte messageType;

    /**
     * 协议版本 (no)
     * 1byte
     */
    private byte version;

    /**
     * 序列化方式类型
     * @see Serializer
     * 1byte
     */
    private byte serializableType;

    /**
     * 内容
     */
    private T content;
}
