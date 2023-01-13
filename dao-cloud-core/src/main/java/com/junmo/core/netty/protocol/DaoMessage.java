package com.junmo.core.netty.protocol;

import com.junmo.core.enums.Constant;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * @author: sucf
 * @date: 2022/10/29 19:49
 * @description: 消息结构载体
 */
@Data
public class DaoMessage {

    /**
     * constructor
     *
     * @param magicNumber
     * @param version
     * @param messageType
     * @param serializableType
     * @param content
     */
    public DaoMessage(byte[] magicNumber, byte version, byte messageType, byte serializableType, Object content) {
        this.magicNumber = magicNumber;
        this.version = version;
        this.messageType = messageType;
        this.serializableType = serializableType;
        this.content = content;
    }

    /**
     * constructor
     *
     * @param version
     * @param messageType
     * @param serializableType
     * @param content
     */
    public DaoMessage(byte version, byte messageType, byte serializableType, Object content) {
        this.magicNumber = Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8);
        this.version = version;
        this.messageType = messageType;
        this.serializableType = serializableType;
        this.content = content;
    }

    //====================================================固定结构====================================================
    /**
     * 魔数
     * 3byte (no)
     */
    private byte[] magicNumber;

    /**
     * 协议版本 (no)
     * 1byte
     */
    private byte version;

    /**
     * 消息类型 (no)
     * 1byte
     */
    private byte messageType;

    /**
     * 序列化方式类型 0：jdk  1：json  2:protobuf  -1:自定义
     * 1byte
     */
    private byte serializableType;

    /**
     * 消息内容长度
     * 4byte
     */
    private int length;
    //====================================================固定结构====================================================

    /**
     * 内容
     */
    private Object content;
}
