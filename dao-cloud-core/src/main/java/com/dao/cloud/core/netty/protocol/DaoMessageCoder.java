package com.dao.cloud.core.netty.protocol;

import com.dao.cloud.core.model.HeartbeatModel;
import com.dao.cloud.core.model.Model;
import com.dao.cloud.core.netty.serialize.DaoSerializer;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sucf
 * @since 1.0
 * 消息协议编码处理
 */
@Slf4j
public class DaoMessageCoder extends MessageToMessageCodec<ByteBuf, DaoMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DaoMessage msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        // 魔数 3bytes
        byteBuf.writeBytes(msg.getMagicNumber());
        // 消息类型 1byte
        byteBuf.writeByte(msg.getMessageType());
        if (msg.getMessageType() == MessageType.PING_PONG_HEART_BEAT_MESSAGE) {
            // heart beat packet
            byteBuf.writeByte(0xff);
            byteBuf.writeByte(0xff);
            byteBuf.writeInt(0x0);
        } else {
            // business message
            // 版本 1byte
            byteBuf.writeByte(msg.getVersion());
            // 序列化 1byte
            byteBuf.writeByte(msg.getSerializableType());
            // 获取内容的字节数组
            DaoSerializer daoSerializer = SerializeStrategyFactory.getSerializer(msg.getSerializableType());
            byte[] bytes = daoSerializer.serialize(msg.getContent());
            // 内容对象长度 int 4bytes
            byteBuf.writeInt(bytes.length);
            // 内容数据
            byteBuf.writeBytes(bytes);
        }
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 魔数 3bytes
        byte[] magicNumber = new byte[3];
        byteBuf.readBytes(magicNumber);
        // 消息类型 1byte
        byte messageType = byteBuf.readByte();
        if (messageType == MessageType.PING_PONG_HEART_BEAT_MESSAGE) {
            // heart beat packet
            list.add(new HeartbeatModel());
        } else {
            // 版本 1byte
            byte version = byteBuf.readByte();
            // 序列化 1byte
            byte serializableType = byteBuf.readByte();
            // 内容字节数 int 4bytes
            int contentLength = byteBuf.readInt();
            byte[] bytes = new byte[contentLength];
            byteBuf.readBytes(bytes, 0, contentLength);
            // 根据不同的序列化方式解析
            DaoSerializer daoSerializer = SerializeStrategyFactory.getSerializer(serializableType);
            Model model = daoSerializer.deserialize(bytes, MessageType.getMessageModel(messageType));
            // 将当前消息存储在通道属性中
            channelHandlerContext.channel().attr(DaoCloudConstant.REQUEST_MESSAGE_ATTR_KEY).set(model);
            list.add(model);
        }
    }
}
