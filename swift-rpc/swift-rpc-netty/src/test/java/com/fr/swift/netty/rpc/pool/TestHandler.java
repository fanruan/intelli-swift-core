package com.fr.swift.netty.rpc.pool;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.bean.InternalRpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * This class created on 2018/8/2
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TestHandler extends SimpleChannelInboundHandler<InternalRpcRequest> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final InternalRpcRequest request) {
        LOGGER.debug("Receive request " + request.getRequestId());
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            response.setResult(1000000);
        } catch (Throwable e) {
            LOGGER.error("handle result failure", e);
            response.setException(e);
        }
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                LOGGER.debug("Send response for request " + request.getRequestId());
            }
        });
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        LOGGER.debug("Rpc client registered!");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        LOGGER.debug("Rpc client unregistered!");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOGGER.debug("Rpc client active!");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        LOGGER.debug("Rpc client read complete!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.debug("Rpc client inactive!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOGGER.error("RPC client caught exception", cause);
    }
}
