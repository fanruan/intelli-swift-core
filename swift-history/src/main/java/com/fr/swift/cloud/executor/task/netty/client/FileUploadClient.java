package com.fr.swift.cloud.executor.task.netty.client;


import com.fr.swift.cloud.executor.task.netty.protocol.FilePacket;
import com.fr.swift.cloud.log.SwiftLoggers;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;


/**
 * @author Hoky
 * @date 2020/11/6
 */
public class FileUploadClient {

    private ChannelFuture future;

    private EventLoopGroup group;

    public boolean connect(String host, int port, FilePacket filePacket) {
        group = new NioEventLoopGroup();  //只需要一个线程组，和服务端有所不同

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {

                        channel.pipeline().addLast(new ObjectEncoder());
                        channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                        channel.pipeline().addLast(new FileUploadClientHandler().fileRegister(filePacket));  //自定义的handler
                    }
                });
        try {
            future = bootstrap.connect(host, port).sync();   //使得链接保持
            if (future.isSuccess()) {
                return true;
            }
        } catch (InterruptedException e) {
            return false;
        }
        return false;
    }

    public boolean writeAndFlush(final FilePacket filePacket) {
        try {
            future.channel().writeAndFlush(filePacket);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
        return true;
    }

    public void closeFuture() {
        if (future != null) {
            future.channel().closeFuture();
        }
    }

    public void shutdownGroup() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8123;
        FilePacket filePacket = new FilePacket();
        File file = new File("/Users/hoky/Work/fanruan/code/swift-gc/target/cubes/202010.zip");
        filePacket.setFile(file);
        filePacket.setStartPos(0);     //要传输的文件的初始信息
        filePacket.setTargetPath("/Users/hoky/Work/fanruan/code/swift-gc-old/target/cubes/202013.zip");
        FileUploadClient fileUploadClient = new FileUploadClient();
        fileUploadClient.connect("127.0.0.1", port, filePacket);
        fileUploadClient.writeAndFlush(filePacket);
    }

}
