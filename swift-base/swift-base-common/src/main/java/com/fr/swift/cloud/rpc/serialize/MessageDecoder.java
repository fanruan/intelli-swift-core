package com.fr.swift.cloud.rpc.serialize;

import com.fr.swift.cloud.rpc.compress.CompressMode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description 特殊反序列化处理流程
 * @since swift-1.2.0
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final int RESERVED_LENGTH = SerializationEncoder.MESSAGE_LENGTH + SerializationEncoder.MESSAGE_ZIP_INFO;

    private final SerializationDecoder decoder;

    private final CompressMode compressMode;

    public MessageDecoder(final SerializationDecoder decoder, final CompressMode compressMode) {
        this.decoder = decoder;
        this.compressMode = compressMode;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RESERVED_LENGTH) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readInt();

        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            boolean isCompressed = in.readBoolean();

            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            if (isCompressed) {
                messageBody = compressMode.decompress(messageBody);
            }
            Object obj = decoder.decode(messageBody);
            out.add(obj);
        }
    }
}

