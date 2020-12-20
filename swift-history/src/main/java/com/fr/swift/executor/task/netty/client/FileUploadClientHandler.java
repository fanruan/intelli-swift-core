package com.fr.swift.executor.task.netty.client;


import com.fr.swift.executor.task.job.impl.MigrateJob;
import com.fr.swift.executor.task.netty.exception.NettyTransferException;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import com.fr.swift.log.SwiftLoggers;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author Hoky
 * @date 2020/11/6
 */
@ChannelHandler.Sharable
public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
    //单例
    private static FileInfoMap fileInfoMap = FileInfoMap.getFileInfoMap();

    public FileUploadClientHandler() {
    }

    public FileUploadClientHandler fileRegister(FilePacket filePacket) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePacket(filePacket);
        fileInfoMap.put(filePacket.getUuid(), fileInfo);
        return this;
    }

    @Override    //当前channel激活的时候的时候触发  优先于channelRead方法执行
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        List<FileInfo> unStartedList = fileInfoMap.getUnStarted();
        for (FileInfo fileInfo : unStartedList) {
            FilePacket filePacket = fileInfo.getFilePacket();
            if (filePacket.getFile().exists()) {
                if (!filePacket.getFile().isFile()) {
                    SwiftLoggers.getLogger().info("Not a file:" + filePacket.getFile());
                    throw new RuntimeException("FilePacket is not a file!");
                }
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(filePacket.getFile(), "r");
            randomAccessFile.seek(filePacket.getStartPos());
//            int lastLength = Integer.MAX_VALUE / 4 > filePacket.getFile().length() ? (int) filePacket.getFile().length() : Integer.MAX_VALUE / 4;
            //每次发送的文件块数的长度
            int lastLength = 3 * 1024;
            byte[] bytes = new byte[lastLength];
            long sendLength = 0;
            sendLength += lastLength;
            int byteRead;
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                filePacket.setEndPos(byteRead);
                filePacket.setBytes(bytes);
                filePacket.setFirst(true);
                if (lastLength <= filePacket.getFile().length()) {
                    filePacket.setEnd(false);
                } else {
                    filePacket.setEnd(true);
                }
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

    @Override  //当前channel从远端读取到数据时触发
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
                if (start != -1 && start == sendLength) {
                    Long remainLength = 0L;
                    try {
                        remainLength = Math.abs(randomAccessFile.length() - start);
                    } catch (Exception e) {
                        isFileClose = true;
                    }
                    if (remainLength == 0L) {
                        isFileClose = true;
                    }
                    if (!isFileClose) {
                        int lastlength = lastLength;
                        if (remainLength < lastlength) {
                            lastlength = remainLength.intValue();
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
                        if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0 && !isFileClose) {
                            filePacket.setEndPos(byteRead);
                            filePacket.setBytes(bytes);
                            filePacket.setFirst(false);
                            FileInfo fileInfo = fileInfoMap.get(uuid);
                            fileInfo.memoryInfo(sendLength, byteRead, randomAccessFile, lastLength, filePacket);
                            fileInfoMap.put(uuid, fileInfo);
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
                        fileInfoMap.transferred(uuid);
                        MigrateJob.countDown();
                        SwiftLoggers.getLogger().info("file migration finished: " + sendLength);
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
