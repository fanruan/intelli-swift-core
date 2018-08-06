package com.fr.swift.netty.rpc.client.sync;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.bean.RpcRequest;
import com.fr.swift.netty.rpc.bean.RpcResponse;
import com.fr.swift.netty.rpc.client.AbstactRpcClientHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@ChannelHandler.Sharable
public class SyncRpcClientHandler extends AbstactRpcClientHandler<RpcResponse> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SyncRpcClientHandler.class);

    public static final String POOL_KEY = "SyncRpcClientHandler";

    private RpcResponse response;

    public SyncRpcClientHandler(String address) {
        super(address);
    }

    private CountDownLatch countDownLatch;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        this.response = response;
        countDownLatch.countDown();
        LOGGER.debug("Receive response : " + response.getRequestId());
    }

    public RpcResponse send(final RpcRequest request) throws Exception {
        countDownLatch = new CountDownLatch(1);
        channel.writeAndFlush(request).sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                LOGGER.debug("Send request : " + request.getRequestId());
            }
        });
        countDownLatch.await();
        return response;
    }
}
