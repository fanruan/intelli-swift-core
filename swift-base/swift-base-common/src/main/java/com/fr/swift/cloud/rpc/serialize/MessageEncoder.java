package com.fr.swift.cloud.rpc.serialize;

import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.rpc.compress.CompressMode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description 特殊序列化处理流程
 * @since swift-1.2.0
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

//    private static long count = 0L;

    private final SerializationEncoder encoder;

    private final CompressMode compressMode;

    public MessageEncoder(final SerializationEncoder encoder, final CompressMode compressMode) {
        this.encoder = encoder;
        this.compressMode = compressMode;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        boolean isCompressed = false;
        byte[] messageBody = encoder.encode(msg);
        int dataLength = messageBody.length;

        if (compressMode.isCompressNeeded() && dataLength > compressMode.getMaxObjectSize()) {
            SwiftLoggers.getLogger().info("It maybe buffer overflow, try compress data.");
            messageBody = compressMode.compress(messageBody);
            isCompressed = true;
        }

        dataLength = messageBody.length;
        out.writeInt(dataLength);
        out.writeBoolean(isCompressed);
        out.writeBytes(messageBody);
//        count += out.readableBytes();
//        SwiftLoggers.getLogger().info("---------------resultSet size : {}", count);
    }
}

