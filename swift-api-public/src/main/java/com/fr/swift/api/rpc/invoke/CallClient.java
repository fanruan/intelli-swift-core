package com.fr.swift.api.rpc.invoke;

import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;
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


/**
 * @author yee
 * @date 2018/8/24
 */
public class CallClient extends SimpleChannelInboundHandler<RpcResponse> implements RpcSender {

    private RpcResponse response;
    private String address;

    public CallClient(String address) {
        this.address = address;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        this.response = rpcResponse;
        ctx.close();
    }

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(
                            new ObjectDecoder(1000000000,
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
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            return response;
        } finally {
            group.shutdownGracefully();
        }

//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bos.write(LENGTH_PLACEHOLDER);
//        ObjectOutputStream oos = new CompactObjectOutputStream(bos);
//        oos.writeObject(request);
//        byte[] datas = bos.toByteArray();
//        int len = datas.length - 4;
//        datas[0] = (byte) (len >>> 24);
//        datas[1] = (byte) (len >>> 16);
//        datas[2] = (byte) (len >>> 8);
//        datas[3] = (byte) len;
//        SocketChannel channel = SocketChannel.open();
//        channel.connect(new InetSocketAddress(host, port));
//        channel.write(ByteBuffer.wrap(datas));
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        int length = 0;
//        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//        while ((length = channel.read(buffer)) > 0) {
//            buffer.flip();
//            bos1.write(buffer.array(), 0, length);
//            buffer.clear();
//            if (length < 1023) {
//                break;
//            }
//        }
//        datas = bos1.toByteArray();
//        byte[] result = Arrays.copyOfRange(datas, 4, datas.length);
//        ObjectInputStream ois = new CompactObjectInputStream(new ByteArrayInputStream(result), Thread.currentThread().getContextClassLoader());
//        Object obj = ois.readObject();
//        if (obj instanceof RpcResponse) {
//            return (RpcResponse) obj;
//        }
//        return null;

    }
}
