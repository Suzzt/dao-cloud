package com.junmo.core.netty.protocol;

import com.junmo.core.model.Model;
import com.junmo.core.netty.serialize.DaoSerializer;
import com.junmo.core.netty.serialize.SerializeStrategyFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: sucf
 * @date: 2022/10/28 20:28
 * @description: 消息协议编码处理
 */
@Slf4j
public class DaoMessageCoder extends MessageToMessageCodec<ByteBuf, DaoMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DaoMessage msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        //====================================write====================================
        //魔数 3bytes
        byteBuf.writeBytes(msg.getMagicNumber());
        //版本 1byte
        byteBuf.writeByte(msg.getVersion());
        //消息类型 1byte
        byteBuf.writeByte(msg.getMessageType());
        //序列化 1byte
        byteBuf.writeByte(msg.getSerializableType());
        //获取内容的字节数组
        DaoSerializer daoSerializer = SerializeStrategyFactory.getSerializer(msg.getMessageType());
        byte[] bytes = daoSerializer.serialize(msg.getContent());
        //内容对象长度 int 4bytes
        byteBuf.writeInt(bytes.length);
        //内容数据
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //====================================read====================================
        //魔数 3bytes
        byte[] magicNumber = new byte[3];
        byteBuf.readBytes(magicNumber);
        //版本 1byte
        byte version = byteBuf.readByte();
        //消息类型 1byte
        byte messageType = byteBuf.readByte();
        //序列化 1byte
        byte serializableType = byteBuf.readByte();
        //内容字节数 int 4bytes
        int contentLength = byteBuf.readInt();
        byte[] bytes = new byte[contentLength];
        byteBuf.readBytes(bytes, 0, contentLength);
        //根据不同的序列化方式解析
        DaoSerializer daoSerializer = SerializeStrategyFactory.getSerializer(messageType);
        Model model = daoSerializer.deserialize(bytes, MessageModelTypeManager.getMessageModel(messageType));
        list.add(model);
    }

}
