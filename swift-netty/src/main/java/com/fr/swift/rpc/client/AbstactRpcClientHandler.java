package com.fr.swift.rpc.client;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
public abstract class AbstactRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstactRpcClientHandler.class);
    protected String host;
    protected int port;
    protected SocketAddress remotePeer;
    protected volatile Channel channel;

    public AbstactRpcClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        this.remotePeer = new InetSocketAddress(host, port);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        LOGGER.info("Rpc client active!");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
        LOGGER.info("Rpc client registered!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.info("Rpc client inactive!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("RPC client caught exception", cause);
        ctx.close();
    }
}
