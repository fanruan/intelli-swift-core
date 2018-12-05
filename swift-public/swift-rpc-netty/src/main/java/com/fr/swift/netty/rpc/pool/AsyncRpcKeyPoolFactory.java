package com.fr.swift.netty.rpc.pool;

import com.fr.swift.netty.rpc.client.async.AsyncRpcClientHandler;
import io.netty.channel.ChannelFuture;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AsyncRpcKeyPoolFactory extends AbstractRpcKeyPoolFactory<AsyncRpcClientHandler> {

    @Override
    public AsyncRpcClientHandler create(String address) throws Exception {
        final AsyncRpcClientHandler clientHandler = new AsyncRpcClientHandler(address);
        ChannelFuture future = bindBootstrap(clientHandler);
        return clientHandler;
    }

    @Override
    public PooledObject<AsyncRpcClientHandler> wrap(AsyncRpcClientHandler asyncRpcClientHandler) {
        return new DefaultPooledObject<AsyncRpcClientHandler>(asyncRpcClientHandler);
    }
}