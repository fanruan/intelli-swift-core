package com.fr.swift.netty.rpc.client.sync;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.bean.RpcRequest;
import com.fr.swift.netty.rpc.bean.RpcResponse;
import com.fr.swift.netty.rpc.client.AbstactRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SyncRpcClientHandler extends AbstactRpcClientHandler {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SyncRpcClientHandler.class);

    private RpcResponse response;

    public SyncRpcClientHandler(String host, int port) {
        super(host, port);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        this.response = response;
        LOGGER.info("Receive response : " + response.getRequestId());
    }

    public RpcResponse send(final RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(
                            new ObjectDecoder(1024 * 10240, ClassResolvers.cacheDisabled(this
                                    .getClass().getClassLoader())));
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(SyncRpcClientHandler.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    LOGGER.info("Send request : " + request.getRequestId());
                }
            });
            channel.closeFuture().sync();
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
