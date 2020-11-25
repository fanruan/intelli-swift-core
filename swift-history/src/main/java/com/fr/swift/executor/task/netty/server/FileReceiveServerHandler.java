package com.fr.swift.executor.task.netty.server;


import com.fr.swift.executor.task.netty.codec.Codec;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.executor.task.netty.protocol.Packet;
import com.fr.swift.log.SwiftLoggers;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class FileReceiveServerHandler extends ChannelInboundHandlerAdapter {

    private OutputStream outputStream;

    private long fileLength;

    private long readLength = 0L;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int type = byteBuf.getInt(0);
        if (type != Codec.TYPE) {
            readLength += byteBuf.readableBytes();
            writeToFile(byteBuf);
            sendComplete(readLength);
        } else {
            Packet packet = Codec.INSTANCE.decode(byteBuf);
            if (packet instanceof FilePacket) {
                FilePacket filePacket = (FilePacket) packet;
                File file = filePacket.getFile();
                SwiftLoggers.getLogger().info("receive file from client: " + file.getName());
                this.fileLength = file.length();
                this.outputStream = new BufferedOutputStream(new FileOutputStream(filePacket.getTargetPath()));
                filePacket.setACK(filePacket.getACK() + 1);
                Codec.INSTANCE.encode(byteBuf, filePacket);
                ctx.writeAndFlush(byteBuf);
            }
        }
    }

    private void writeToFile(ByteBuf byteBuf) throws IOException {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        outputStream.write(bytes);
        byteBuf.release();
    }

    private void sendComplete(long readLength) throws IOException {
        if (readLength >= fileLength) {
            SwiftLoggers.getLogger().info("file receive successfully...");
            outputStream.close();
        }
    }

}
