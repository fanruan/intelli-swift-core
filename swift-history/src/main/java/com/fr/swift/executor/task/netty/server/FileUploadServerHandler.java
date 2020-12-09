package com.fr.swift.executor.task.netty.server;

import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.log.SwiftLoggers;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * * @author Hoky
 * * @date 2020/11/26
 */
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {
    public static final int LENGTH = 100000;
    private int byteRead;
    private volatile Long start = 0L;
    private RandomAccessFile randomAccessFile;
    private volatile boolean fileWriteLock = false;
    private volatile boolean isFirst = true;

    //当前channel从远端读取到数据时执行 
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FilePacket) {
            FilePacket filePacket = (FilePacket) msg;
            if (filePacket.isFirst() == this.isFirst) {
                if (filePacket.isFirst()) {
                    isFirst = false;
                }
                byte[] bytes = filePacket.getBytes();
                byteRead = filePacket.getEndPos();
                if (!fileWriteLock) {
                    randomAccessFile = new RandomAccessFile(new File(filePacket.getTargetPath()), "rw");
                    randomAccessFile.seek(start);
                    randomAccessFile.write(bytes);
                    randomAccessFile.close();
                    SwiftLoggers.getLogger().info("migration server receive file: {} mb", byteRead / LENGTH);
                }
                start = start + byteRead;
                //                ctx.writeAndFlush(new ACKPacket(start,!this.isNext)); 
                ctx.writeAndFlush(start);
                if (filePacket.isEnd()) {
                    fileWriteLock = true;
                    SwiftLoggers.getLogger().info("finished transfer to " + filePacket.getTargetPath());
                }
            }
        }
    }

    @Override  //ChannelHandler回调方法出现异常时被回调 
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
