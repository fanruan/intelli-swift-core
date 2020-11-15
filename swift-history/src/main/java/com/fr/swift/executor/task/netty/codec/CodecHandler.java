package com.fr.swift.executor.task.netty.codec;


import com.fr.swift.executor.task.netty.protocol.Packet;
import com.fr.swift.log.SwiftLoggers;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@ChannelHandler.Sharable
public class CodecHandler extends MessageToMessageCodec<ByteBuf, Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if (o instanceof Packet) {
            ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
            Codec.INSTANCE.encode(byteBuf, (Packet) o);
            list.add(byteBuf);
        } else {
            SwiftLoggers.getLogger().info("File ByteBuf need't encode");
            // ctx.writeAndFlush(o);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.getInt(0) == Codec.TYPE) {
            SwiftLoggers.getLogger().info("decode FilePacket");
            list.add(Codec.INSTANCE.decode(byteBuf));
        } else {
            SwiftLoggers.getLogger().info("File ByteBuf need't decode");
            // list.add(byteBuf);
            ctx.fireChannelRead(byteBuf);
        }
    }
}
