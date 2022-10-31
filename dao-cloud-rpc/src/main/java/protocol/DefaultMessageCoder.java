package protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import model.DaoMessage;
import serializable.ClassCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: sucf
 * @date: 2022/10/28 20:28
 * @description: 消息协议编码处理
 */
@Slf4j
public class DefaultMessageCoder extends MessageToMessageCodec<ByteBuf, DaoMessage> {

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
        byte[] bytes;
        if (msg.getSerializableType() == 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(msg.getContent());
            bytes = bos.toByteArray();
        } else if (msg.getSerializableType() == 1) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            String contentJson = gson.toJson(msg.getContent());
            bytes = contentJson.getBytes(StandardCharsets.UTF_8);
        } else {
            throw new RuntimeException("嘿嘿");
        }
        //内容对象长度 int 4bytes
        byteBuf.writeInt(bytes.length);
        //内容数据
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //====================================read====================================
        //魔数 3bytes
        byte[] magicNumber = new byte[3];
        byteBuf.readBytes(magicNumber);
        log.debug("magicNumber={}", new String(magicNumber));
        //版本 1byte
        byte version = byteBuf.readByte();
        log.debug("version={}", version);
        //消息类型 1byte
        byte messageType = byteBuf.readByte();
        if (messageType == 0) {
            //// TODO: 2022/10/31  心跳
        } else if (messageType == 1) {
            //rpc
            //序列化 1byte
            byte serializableType = byteBuf.readByte();
            log.debug("serializableType={}", serializableType);
            //内容字节数 int 4bytes
            int contentLength = byteBuf.readInt();
            log.debug("contentLength={}", contentLength);
            //根据不同的序列化方式解析
            byte[] content = new byte[contentLength];
            byteBuf.readBytes(content, 0, contentLength);
            Object o;
            if (serializableType == 0) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(content));
                o = ois.readObject();
            } else if (serializableType == 1) {
                String contentJson = new String(content);
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                o = gson.fromJson(contentJson, Object.class);
            } else {
                throw new RuntimeException("嘿嘿");
            }
            list.add(o);
        }
    }

}
