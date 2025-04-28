package com.dao.cloud.core.netty.protocol;

import com.dao.cloud.core.exception.UnsupportedVersionException;
import com.dao.cloud.core.model.HeartbeatModel;
import com.dao.cloud.core.model.Model;
import com.dao.cloud.core.netty.serialize.DaoSerializer;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/10/28 20:28
 * 消息协议编码处理
 */
@Slf4j
public class DaoMessageCoder extends MessageToMessageCodec<ByteBuf, DaoMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DaoMessage msg, List<Object> out) {
        ByteBuf buf = ctx.alloc().buffer();
        try {
            // 固定头魔数值
            buf.writeBytes(DaoCloudConstant.MAGIC_NUMBER);
            buf.writeByte(msg.getMessageType());

            if (msg.getMessageType() != MessageType.PING_PONG_HEART_BEAT_MESSAGE) {
                // 校验协议版本(现在没有什么用)
                if (msg.getVersion() != DaoCloudConstant.PROTOCOL_VERSION_1) {
                    throw new UnsupportedVersionException("Unsupported version: " + msg.getVersion());
                }

                // 业务头
                buf.writeByte(msg.getVersion());
                buf.writeByte(msg.getSerializableType());

                // 序列化内容
                DaoSerializer serializer = SerializeStrategyFactory.getSerializer(msg.getSerializableType());
                byte[] content = serializer.serialize(msg.getContent());

                // 写入Varint长度
                writeVarint(buf, content.length);
                buf.writeBytes(content);
            }

            out.add(buf);
        } catch (Exception e) {
            buf.release();
            handleEncodeError(ctx, e);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf frame, List<Object> out) {
        try {
            frame.skipBytes(DaoCloudConstant.MAGIC_NUMBER_LENGTH); // 跳过魔数

            byte messageType = frame.readByte();
            if (messageType == MessageType.PING_PONG_HEART_BEAT_MESSAGE) {
                out.add(new HeartbeatModel());
                return;
            }

            // 版本检查
            byte version = frame.readByte();
            if (version != DaoCloudConstant.PROTOCOL_VERSION_1) {
                throw new UnsupportedVersionException("Received unsupported version: " + version);
            }

            // 读取序列化类型
            byte serializableType = frame.readByte();

            // 读取内容
            int contentLength = readVarint(frame);
            byte[] content = new byte[contentLength];
            frame.readBytes(content);

            // 反序列化
            DaoSerializer serializer = SerializeStrategyFactory.getSerializer(serializableType);
            Model model = serializer.deserialize(content, MessageType.getMessageModel(messageType));

            ctx.channel().attr(DaoCloudConstant.REQUEST_MESSAGE_ATTR_KEY).set(model);
            out.add(model);
        } catch (Exception e) {
            handleDecodeError(frame, e);
        }
    }

    private void writeVarint(ByteBuf buf, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                buf.writeByte(value);
                return;
            }
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }

    private int readVarint(ByteBuf buf) {
        int result = 0;
        int shift = 0;
        int bytes = 0;
        while (true) {
            byte b = buf.readByte();
            bytes++;
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) break;
            shift += 7;
            if (bytes > 5) {
                throw new CorruptedFrameException("Varint overflow");
            }
        }
        return result;
    }

    private void handleEncodeError(ChannelHandlerContext ctx, Exception e) {
        log.error("Encode error from {}", ctx.channel().remoteAddress(), e);
        ctx.close();
    }

    private void handleDecodeError(ByteBuf frame, Exception e) {
        log.error("Decode error", e);
        ReferenceCountUtil.release(frame);
        if (e instanceof UnsupportedVersionException) {
            throw (UnsupportedVersionException) e;
        }
    }
}