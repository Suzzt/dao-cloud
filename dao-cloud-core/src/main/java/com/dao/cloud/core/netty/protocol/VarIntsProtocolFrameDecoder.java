package com.dao.cloud.core.netty.protocol;

import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sucf
 * @date 2022/10/28 20:44
 * @since 1.0.0
 * dao的协议定义
 * dao(魔术)+消息类型(1b)+dao协议版本(1b)+序列化类型(1b)+Varint长度(1-5b)+内容
 */
@Slf4j
public class VarIntsProtocolFrameDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        in.markReaderIndex();

        try {
            // 基本长度检查(魔数3b+消息类型1b)
            if (in.readableBytes() < DaoCloudConstant.MAGIC_NUMBER_LENGTH + 1) {
                return;
            }

            // 魔数校验
            if (!checkMagicNumber(in)) {
                log.warn("Invalid magic number from {}", ctx.channel().remoteAddress());
                ctx.close();
                return;
            }

            // 预读消息类型
            final int typeIndex = in.readerIndex() + DaoCloudConstant.MAGIC_NUMBER_LENGTH;
            final byte messageType = in.getByte(typeIndex);

            if (messageType == MessageType.PING_PONG_HEART_BEAT_MESSAGE) {
                // 处理心跳消息
                handleHeartbeat(in, out);
                return;
            }

            // 处理业务消息
            processBusinessFrame(in, out);
        } catch (Exception e) {
            handleDecodeError(in, e);
        }
    }

    private boolean checkMagicNumber(ByteBuf in) {
        for (int i = 0; i < DaoCloudConstant.MAGIC_NUMBER_LENGTH; i++) {
            if (in.getByte(in.readerIndex() + i) != DaoCloudConstant.MAGIC_NUMBER[i]) {
                return false;
            }
        }
        return true;
    }

    private void handleHeartbeat(ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= DaoCloudConstant.HEARTBEAT_FRAME_LENGTH) {
            out.add(in.readRetainedSlice(DaoCloudConstant.HEARTBEAT_FRAME_LENGTH));
        }
    }

    private void processBusinessFrame(ByteBuf in, List<Object> out) {
        // 跳过魔数和消息类型
        in.skipBytes(DaoCloudConstant.MAGIC_NUMBER_LENGTH + 1);

        // 检查基础字段
        if (in.readableBytes() < 2) {
            in.resetReaderIndex();
            return;
        }

        // 读取版本和序列化类型
        final byte version = in.readByte();
        final byte serializableType = in.readByte();

        // 读取Varint长度
        int varintBytes = 0;
        int contentLength = 0;
        while (true) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }
            byte b = in.readByte();
            varintBytes++;
            contentLength |= (b & 0x7F) << (7 * (varintBytes - 1));
            if ((b & 0x80) == 0) break;
            if (varintBytes > 5) {
                throw new CorruptedFrameException("Varint overflow");
            }
        }

        // 计算总帧长度
        int totalLength = DaoCloudConstant.MAGIC_NUMBER_LENGTH  // 魔数3
                + 1                                     // 消息类型1
                + 2                                     // 版本1+序列化1
                + varintBytes                           // Varint头
                + contentLength;                        // 内容长度

        // 长度校验
        if (totalLength > DaoCloudConstant.MAX_FRAME_LENGTH) {
            throw new CorruptedFrameException("Frame too large: " + totalLength);
        }

        // 检查数据完整性
        if (in.readableBytes() < contentLength) {
            in.resetReaderIndex();
            return;
        }

        // 切片完整帧
        in.resetReaderIndex();
        out.add(in.readRetainedSlice(totalLength));
    }

    private void handleDecodeError(ByteBuf in, Exception e) {
        log.error("Decode error: {}", e.getMessage());
        in.resetReaderIndex();
        if (e instanceof CorruptedFrameException) {
            throw new RuntimeException(e);
        }
    }
}