package com.fr.swift.netty.rpc.client.async;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.bean.RpcRequest;
import com.fr.swift.netty.rpc.bean.RpcResponse;
import com.fr.swift.netty.rpc.client.AbstactRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AsyncRpcClientHandler extends AbstactRpcClientHandler {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AsyncRpcClientHandler.class);

    private Map<String, RpcFuture> pendingRPC = new ConcurrentHashMap<String, RpcFuture>();

    EventLoopGroup group = new NioEventLoopGroup();

    public AsyncRpcClientHandler(String host, int port) {
        super(host, port);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        String requestId = response.getRequestId();
        LOGGER.info("Receive response : " + requestId);
        RpcFuture rpcFuture = pendingRPC.get(requestId);
        if (rpcFuture != null) {
            pendingRPC.remove(requestId);
            rpcFuture.done(response);
        }
        group.shutdownGracefully();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RpcFuture send(final RpcRequest request) throws Exception {
        RpcFuture rpcFuture = new RpcFuture(request);
        pendingRPC.put(request.getRequestId(), rpcFuture);
        if (channel == null || !channel.isActive()) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(
                            new ObjectDecoder(10240 * 1024, ClassResolvers.cacheDisabled(this
                                    .getClass().getClassLoader())));
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(AsyncRpcClientHandler.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture channelFuture = bootstrap.connect(remotePeer).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture channelFuture) {
                    if (channelFuture.isSuccess()) {
                        LOGGER.info("Successfully connect to remote server. remote peer = " + remotePeer);
                    }
                }
            });
            channel = channelFuture.channel();
        }
        final CountDownLatch latch = new CountDownLatch(1);
        channel.writeAndFlush(request).sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                LOGGER.info("Send request : " + request.getRequestId());
                latch.countDown();
            }
        });
        return rpcFuture;
    }
}
