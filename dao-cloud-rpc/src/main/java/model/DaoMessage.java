package model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/29 19:49
 * @description: 消息结构载体
 */
@Data
public class DaoMessage {

    //====================================================固定结构====================================================
    /**
     * 魔数
     * 3byte
     */
    private byte[] magicNumber;

    /**
     * 协议版本
     * 1byte
     */
    private byte version;

    /**
     * 消息类型 0:心跳 1:rpc ...
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
    ////====================================================固定结构====================================================

    /**
     * 内容
     */
    private Object content;
}
