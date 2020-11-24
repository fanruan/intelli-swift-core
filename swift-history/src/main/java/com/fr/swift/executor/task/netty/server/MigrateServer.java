package com.fr.swift.executor.task.netty.server;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLoggers;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @author Hoky
 * @date 2020/11/6
 */
@SwiftBean(name = "migrateServer")
public class MigrateServer {

    private String serviceAddress;

    public MigrateServer() {
//        SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);
//        this.serviceAddress = nodeInfoService.getNodeInfo(SwiftProperty.get().getMachineId()).getMigServerAddress();
    }

    public void start() {
//        String[] addressArray = Strings.split(serviceAddress, ":");
//        String ip = addressArray[0];
//        int port = Integer.parseInt(addressArray[1]);
        String ip = "127.0.0.1";
        int port = 8123;
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
                        channel.pipeline().addLast(new FileReceiveServerHandler());
//                        channel.pipeline().addLast(new DecodeHandler());
//                        channel.pipeline().addLast(new EncodeHandler());
//                        channel.pipeline().addLast(new FilePacketServerHandler());
                        channel.pipeline().addLast(new JoinClusterRequestHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            SwiftLoggers.getLogger().info("Migrate server started on ip:" + ip + ", port :" + port);
            if (future.isSuccess()) {
                SwiftLoggers.getLogger().info("Migrate server start success");
            }
            future.channel().closeFuture().sync(); //保证了服务一直启动，相当于一个死循环
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        MigrateServer migrateServer = new MigrateServer();
        migrateServer.start();
    }
}
