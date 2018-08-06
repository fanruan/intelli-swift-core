package com.fr.swift.netty.rpc.client.async;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.bean.RpcRequest;
import com.fr.swift.netty.rpc.bean.RpcResponse;
import com.fr.swift.netty.rpc.client.AbstactRpcClientHandler;
import com.fr.swift.netty.rpc.pool.AsyncRpcPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

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
@ChannelHandler.Sharable
public class AsyncRpcClientHandler extends AbstactRpcClientHandler<RpcFuture> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AsyncRpcClientHandler.class);

    public static final String POOL_KEY = "AsyncRpcClientHandler";

    private Map<String, RpcFuture> pendingRPC = new ConcurrentHashMap<String, RpcFuture>();

    public AsyncRpcClientHandler(String address) {
        super(address);
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
        AsyncRpcPool.getIntance().returnObject(address, this);
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RpcFuture send(final RpcRequest request) throws Exception {
        RpcFuture rpcFuture = new RpcFuture(request);
        pendingRPC.put(request.getRequestId(), rpcFuture);
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
