package com.fr.swift.executor.task.netty.server;


import com.fr.swift.executor.task.netty.protocol.FilePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author Hoky
 * @date 2020/11/26
 */
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {
    private int byteRead;
    private volatile Long start = 0L;
    private RandomAccessFile randomAccessFile;

    @Override   //当前channel从远端读取到数据时执行
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FilePacket) {
            FilePacket filePacket = (FilePacket) msg;
            byte[] bytes = filePacket.getBytes();
            byteRead = filePacket.getEndPos();
            randomAccessFile = new RandomAccessFile(new File(filePacket.getTargetPath()), "rw");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + byteRead;
            ctx.writeAndFlush(start);
            if (filePacket.isEnd()) {
                randomAccessFile.close();
            }
        }
    }

    @Override  //ChannelHandler回调方法出现异常时被回调
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
