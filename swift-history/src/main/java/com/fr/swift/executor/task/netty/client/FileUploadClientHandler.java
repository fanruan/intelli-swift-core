package com.fr.swift.executor.task.netty.client;


import com.fr.swift.executor.task.job.impl.MigrateJob;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.log.SwiftLoggers;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.RandomAccessFile;

/**
 * @author Hoky
 * @date 2020/11/6
 */
@ChannelHandler.Sharable
public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {

    private int byteRead;
    private volatile Long start = 0l;   //使用Long 当传输的文件大于2G时，Integer类型会不够表达文件的长度
    private volatile int lastLength = 0;
    private volatile long sendLength = 0L;
    private volatile boolean isNext = false;
    public RandomAccessFile randomAccessFile;
    private FilePacket filePacket;

    //构造器，FilePacket作为参数
    public FileUploadClientHandler(FilePacket filePacket) {
        this.filePacket = filePacket;
    }

    @Override    //当前channel激活的时候的时候触发  优先于channelRead方法执行  （我的理解，只执行一次）
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (filePacket.getFile().exists()) {
            if (!filePacket.getFile().isFile()) {
                SwiftLoggers.getLogger().info("Not a file:" + filePacket.getFile());
                throw new RuntimeException("FilePacket is not a file!");
            }
        }
        randomAccessFile = new RandomAccessFile(filePacket.getFile(), "r");
        randomAccessFile.seek(filePacket.getStartPos());
        lastLength = Integer.MAX_VALUE / 4 > filePacket.getFile().length() ? (int) filePacket.getFile().length() : Integer.MAX_VALUE / 4; //每次发送的文件块数的长度
//        lastLength = 3*1024*1024;
        byte[] bytes = new byte[lastLength];
        sendLength += lastLength;
        if ((byteRead = randomAccessFile.read(bytes)) != -1) {
            filePacket.setEndPos(byteRead);
            filePacket.setBytes(bytes);
            filePacket.setFirst(true);
            isNext = true;
            if (lastLength <= filePacket.getFile().length()) {
                filePacket.setEnd(false);
            } else {
                filePacket.setEnd(true);
            }
            ctx.writeAndFlush(filePacket);
        } else {
            SwiftLoggers.getLogger().info("file already read!");
        }
    }

    @Override  //当前channel从远端读取到数据时触发
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Long) {    //客户端发送FilePacket 到服务端，服务端处理完文件当前部分的数据，返回下次文件段的起始位置
            start = (Long) msg;
            boolean isFileClose = false;
            if (start != -1 && start == sendLength) {
                Long a = 0L;
                try {
                    a = Math.abs(randomAccessFile.length() - start);
                } catch (Exception e) {
                    isFileClose = true;
                }
                if (!isFileClose) {
                    int lastlength = lastLength;
                    if (a < lastlength) {
                        lastlength = a.intValue();
                        filePacket.setEnd(true);
                    } else {
                        filePacket.setEnd(false);
                    }
                    byte[] bytes = new byte[lastlength];
                    if ((randomAccessFile.length() - start) > 0) {
                        sendLength += lastlength;
                        randomAccessFile = new RandomAccessFile(filePacket.getFile(), "r");
                        randomAccessFile.seek(start);  //将服务端返回的数据设置此次读操作，文件的起始偏移量
                    }
                    //这个判断关闭判断调整存在漏洞
                    if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0 && !isFileClose) {
                        filePacket.setEndPos(byteRead);
                        filePacket.setBytes(bytes);
                        filePacket.setFirst(false);
                        ctx.writeAndFlush(filePacket);
                        if (filePacket.isEnd()) {
                            randomAccessFile.close();
                        }
                    } else {
                        ctx.close();
                        MigrateJob.countDown();
                        SwiftLoggers.getLogger().info("file migration finished: " + byteRead);
                    }
                }
            }
        }
    }

    @Override  //在当前ChannelHandler回调方法出现异常时被回调
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
