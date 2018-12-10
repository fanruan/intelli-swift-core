package com.fr.swift.api.rpc.invoke;

import com.fr.swift.api.request.RpcSender;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.CountDownLatch;


/**
 * @author yee
 * @date 2018/8/24
 */
public class CallClient extends SimpleChannelInboundHandler<RpcResponse> implements RpcSender {

    private RpcResponse response;
    private String address;
    private int maxFrameSize;
    private Channel channel;
    private EventLoopGroup group;
    private CountDownLatch latch;

    public CallClient(String address, int maxFrameSize) {
        this.address = address;
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        this.response = rpcResponse;
        latch.countDown();
    }

    public void bind() throws InterruptedException {
        group = new NioEventLoopGroup(1);
        // 创建并初始化 Netty 客户端 Bootstrap 对象
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(
                        new ObjectDecoder(maxFrameSize,
                                ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(CallClient.this); // 处理 RPC 响应
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        String[] array = address.split(":");
        String host = null;
        int port = 7000;
        host = array[0];
        if (array.length > 1) {
            port = Integer.parseInt(array[1]);
        }
        // 连接 RPC 服务器
        ChannelFuture future = bootstrap.connect(host, port).sync();
        // 写入 RPC 请求数据并关闭连接
        channel = future.channel();
    }

    public void close() {
        group.shutdownGracefully();
    }

    public boolean isActive() {
        return null != channel && channel.isActive();
    }

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {
        latch = new CountDownLatch(1);
        channel.writeAndFlush(request).sync();
        latch.await();
        return response;

    }
}
