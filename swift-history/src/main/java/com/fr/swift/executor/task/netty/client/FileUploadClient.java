package com.fr.swift.executor.task.netty.client;


import com.fr.swift.executor.task.netty.codec.DecodeHandler;
import com.fr.swift.executor.task.netty.codec.EncodeHandler;
import com.fr.swift.executor.task.netty.protocol.FilePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author Hoky
 * @date 2020/11/6
 */
public class FileUploadClient {

    private ChannelFuture future;

    private EventLoopGroup group;

    public boolean connect(String host, int port, final FilePacket filePacket) {
        group = new NioEventLoopGroup();  //只需要一个线程组，和服务端有所不同

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new FileSendClientHandler());
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new EncodeHandler());
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new LoginResponseHandler());
                    }
                });
        try {
            future = bootstrap.connect(host, port).sync();   //使得链接保持
            if (future.isSuccess()) {
                future.channel().writeAndFlush(filePacket);
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeFuture() {
        future.channel().closeFuture();
    }

    public void shutdownGroup() {
        group.shutdownGracefully();
    }

//    public static void main(String[] args) {
//        FileUploadClient fileUploadClient = new FileUploadClient();
//        fileUploadClient.connect("127.0.0.1",8123,new FilePacket(new File("/Users/hoky/Work/fanruan/code/swift-gc/target/cubes/202010.zip")));
//    }
}
