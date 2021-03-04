package com.fr.swift.cloud.executor.task.netty.server;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.service.SwiftNodeInfoService;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.util.Strings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


/**
 * @author Hoky
 * @date 2020/11/6
 */
@SwiftBean(name = "migrateServer")
public class MigrateServer {

    public void start() {
        String serviceAddress = SwiftContext.get().getBean(SwiftNodeInfoService.class).getOwnNodeInfo().getMigServerAddress();
        String[] addressArray = Strings.split(serviceAddress, ":");
        String ip = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        EventLoopGroup boosGroup = new NioEventLoopGroup(); //服务端的管理线程
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //服务端的工作线程

        //ServerBootstrap负责初始化netty服务器，并且开始监听端口的socket请求
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boosGroup, workerGroup)   //绑定管理线程和工作线程
                .channel(NioServerSocketChannel.class)   //ServerSocketChannelFactory 有两种选择，一种是NioServerSocketChannelFactory，一种是OioServerSocketChannelFactory。
                .option(ChannelOption.SO_BACKLOG, 124)  //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new ObjectEncoder());
                        channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));
                        channel.pipeline().addLast(new FileUploadServerHandler()); // 自定义Handler
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            future.channel().closeFuture().sync(); //保证了服务一直启动，相当于一个死循环
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
