package com.fr.swift.cloud.executor.task.netty.client;


import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.service.SwiftNodeInfoService;
import com.fr.swift.cloud.executor.task.job.impl.MigrateJob;
import com.fr.swift.cloud.executor.task.netty.exception.NettyTransferException;
import com.fr.swift.cloud.executor.task.netty.exception.NettyTransferOvertimeException;
import com.fr.swift.cloud.executor.task.netty.protocol.FilePacket;
import com.fr.swift.cloud.log.SwiftLoggers;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;

/**
 * @author Hoky
 * @date 2020/11/6
 */
@ChannelHandler.Sharable
public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
    private static final int BLOCK_LENGTH = Integer.MAX_VALUE / 200;
    //文件信息的缓存，缓存不同的传输，是一个单例。
    private static FileInfoMap fileInfoMap = FileInfoMap.getFileInfoMap();
    private int limitTransferHour;

    public FileUploadClientHandler() {
        limitTransferHour = SwiftContext.get().getBean(SwiftNodeInfoService.class).getOwnNodeInfo().getLimitTransferHour();
    }

    public FileUploadClientHandler fileRegister(FilePacket filePacket) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePacket(filePacket);
        fileInfoMap.put(filePacket.getUuid(), fileInfo);
        return this;
    }

    /**
     * @description: 1.当前channel激活的时候的时候触发  优先于channelRead方法执行；
     * 2.将文件分成BLOCK_LENGTH大小；
     * 3.存储文件信息，然后发送；
     * 4.添加一个监听器，当发送失败的时候抛出异常。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        List<FileInfo> unStartedList = fileInfoMap.getUnStarted();
        for (FileInfo fileInfo : unStartedList) {
            FilePacket filePacket = fileInfo.getFilePacket();
            if (filePacket.getFile().exists() && !filePacket.getFile().isFile()) {
                SwiftLoggers.getLogger().info("Not a file:" + filePacket.getFile());
                throw new RuntimeException("FilePacket is not a file!");
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(filePacket.getFile(), "r");
            randomAccessFile.seek(filePacket.getStartPos());
            //每次发送的文件块数的长度
            int lastLength = BLOCK_LENGTH > filePacket.getFile().length() ? (int) filePacket.getFile().length() : BLOCK_LENGTH;
            byte[] bytes = new byte[lastLength];
            long sendLength = 0;
            sendLength += lastLength;
            int byteRead;
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                filePacket.setEndPos(byteRead)
                        .setBytes(bytes)
                        .setFirst(true)
                        .setEnd(lastLength >= filePacket.getFile().length());
                fileInfo.memoryInfo(sendLength, byteRead, randomAccessFile, lastLength, filePacket);
                fileInfoMap.put(filePacket.getUuid(), fileInfo);
                fileInfoMap.get(filePacket.getUuid()).startTransfer();
                ChannelFuture channelFuture = ctx.writeAndFlush(filePacket);
                channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                    if (!channelFuture1.isSuccess()) {
                        MigrateJob.countDown();
                        throw new NettyTransferException();
                    }
                });
            } else {
                SwiftLoggers.getLogger().info("file already read!");
            }
        }
    }

    //，将文件分割出BLOCK_LENGTH的大小

    /**
     * @description: 1.当前channel从远端读取到数据时触发；
     * 2.将远端读的消息解析成start位置和uuid；
     * 3.根据uuid获得之前缓存的文件信息；
     * 4.根据判断start判断文件是否传输完成或者是否超时，如果传输完成或超时则结束传输；
     * 4.若正常流程，则将文件分成BLOCK_LENGTH大小发送，逻辑与上相似。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {    //客户端发送FilePacket 到服务端，服务端处理完文件当前部分的数据，返回下次文件段的起始位置
            String message = (String) msg;
            String[] msgs = message.split("/");
            if (msgs.length > 1) {
                long start = Long.parseLong(msgs[0]);
                String uuid = msgs[1];
                long sendLength = fileInfoMap.getSendLength(uuid);
                RandomAccessFile randomAccessFile = fileInfoMap.getRandomAccessFile(uuid);
                int lastLength = fileInfoMap.getLastLength(uuid);
                FilePacket filePacket = fileInfoMap.getFilePacket(uuid);
                int byteRead;
                boolean isFileClose = false;
                boolean isOverTime = new Date(System.currentTimeMillis()).getHours() >= limitTransferHour;
                if (start != -1 && start == sendLength) {
                    Long remainLength = 0L;
                    //如果randomAccessFile关闭了或者剩余传输的长度为0，则将文件关闭读操作
                    try {
                        remainLength = randomAccessFile.length() - start;
                    } catch (Exception e) {
                        isFileClose = true;
                    }
                    if (remainLength <= 0L) {
                        isFileClose = true;
                    }
                    //如果超时了或者文件锁了就不执行
                    if ((!isFileClose) && (!isOverTime)) {
                        int lastlength = lastLength;
                        if (remainLength < lastlength) {
                            lastlength = remainLength.intValue();
                            filePacket.setEnd(true);
                        } else {
                            filePacket.setEnd(false);
                        }
                        byte[] bytes = new byte[lastlength];
                        if (remainLength > 0) {
                            sendLength += lastlength;
                            randomAccessFile.seek(start);  //将服务端返回的数据设置此次读操作，文件的起始偏移量
                        }
                        if ((byteRead = randomAccessFile.read(bytes)) != -1 && !isFileClose) {
                            filePacket.setEndPos(byteRead)
                                    .setBytes(bytes)
                                    .setFirst(false);
                            //存储该次分割的信息
                            fileInfoMap.get(uuid).memoryInfo(sendLength, byteRead, randomAccessFile, lastLength, filePacket);
                            ChannelFuture channelFuture = ctx.writeAndFlush(filePacket);
                            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                                if (!channelFuture1.isSuccess()) {
                                    MigrateJob.countDown();
                                    throw new NettyTransferException();
                                }
                            });
                            if (filePacket.isEnd()) {
                                randomAccessFile.close();
                            }
                        }
                    } else {
                        ctx.close();
                        if (isOverTime || (remainLength < 0L)) {
                            SwiftLoggers.getLogger().error("file migration overtime");
                            MigrateJob.countDown();
                            throw new NettyTransferOvertimeException();
                        } else {
                            fileInfoMap.transferred(uuid);
                            SwiftLoggers.getLogger().info("file migration finished: " + sendLength + " b");
                            MigrateJob.countDown();
                        }
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

    public static boolean isTransfer(String uuid) {
        return fileInfoMap.isTransferred(uuid);
    }

}
