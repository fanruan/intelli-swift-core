package com.fr.swift.executor.task.netty.server;


import com.fr.swift.executor.task.netty.codec.Codec;
import com.fr.swift.log.SwiftLoggers;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class FileReceiveServerHandler extends ChannelInboundHandlerAdapter {

    private static FileOutputStream outputStream;

    private static long fileLength;

    private static long readLength;

    public static FileOutputStream getOutputStream() {
        return outputStream;
    }

    public static void setOutputStream(FileOutputStream outputStream) {
        FileReceiveServerHandler.outputStream = outputStream;
    }

    public static long getFileLength() {
        return fileLength;
    }

    public static void setFileLength(long fileLength) {
        FileReceiveServerHandler.fileLength = fileLength;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int type = byteBuf.getInt(0);
        if (type != Codec.TYPE) {
            readLength += byteBuf.readableBytes();
            writeToFile(byteBuf);
            sendComplete(readLength);
        } else {
            super.channelRead(ctx, msg);
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

    public static void clearReadLength() {
        readLength = 0L;
    }
}
