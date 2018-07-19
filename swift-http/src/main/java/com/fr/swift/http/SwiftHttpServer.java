package com.fr.swift.http;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.http.handler.ServletChannelInitializer;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.NettyServiceStarter;
import com.fr.swift.property.SwiftProperty;
import com.fr.third.jodd.util.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftHttpServer implements NettyServiceStarter {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHttpServer.class);

    private SwiftProperty swiftProperty = SwiftContext.getInstance().getBean(SwiftProperty.class);

    private String httpAddress;

    public SwiftHttpServer() {
        this.httpAddress = swiftProperty.getHttpAddress();
    }

    private void startBootstrap() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ServletChannelInitializer());
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
    public void start() throws Exception {
        startBootstrap();
    }

    @Override
    public void stop() {
    }
}
