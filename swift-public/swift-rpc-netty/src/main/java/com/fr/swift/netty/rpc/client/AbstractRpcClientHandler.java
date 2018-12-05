package com.fr.swift.netty.rpc.client;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractRpcClientHandler<T> extends SimpleChannelInboundHandler<RpcResponse> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractRpcClientHandler.class);
    protected String address;
    protected String host;
    protected int port;
    protected SocketAddress remotePeer;
    protected volatile Channel channel;
    protected EventLoopGroup group;

    public AbstractRpcClientHandler(String address) {
        this.address = address;
        String[] array = address.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        this.host = host;
        this.port = port;
        this.remotePeer = new InetSocketAddress(host, port);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        LOGGER.debug("Rpc client active!");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
        LOGGER.debug("Rpc client registered!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.close();
        LOGGER.debug("Rpc client inactive!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("RPC client caught exception", cause);
        ctx.close();
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    public void closeChannel() {
        if (channel != null) {
            channel.close();
        }
    }

    public void setGroup(EventLoopGroup group) {
        this.group = group;
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public abstract T send(final RpcRequest request) throws Exception;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
