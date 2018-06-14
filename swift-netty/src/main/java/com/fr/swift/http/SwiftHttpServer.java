package com.fr.swift.http;

import com.fr.swift.http.handler.DispatcherServletChannelInitializer;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.NettyServiceStarter;
import com.fr.third.jodd.util.StringUtil;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class SwiftHttpServer implements NettyServiceStarter {

    private ScheduledExecutorService serverServiceExector = Executors.newScheduledThreadPool(1);

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHttpServer.class);

    private String httpAddress;

    @Autowired
    public SwiftHttpServer(@Value("${swift.http_server_address}") String httpAddress) {
        this.httpAddress = httpAddress;
    }

    private void startBootstrap() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new DispatcherServletChannelInitializer());
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] addressArray = StringUtil.split(httpAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            LOGGER.info("HTTP srver started on ip:" + ip + ", port :" + port);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void start() {
        serverServiceExector.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    startBootstrap();
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        });
    }

    @Override
    public void stop() {
        serverServiceExector.shutdownNow();
    }
}
