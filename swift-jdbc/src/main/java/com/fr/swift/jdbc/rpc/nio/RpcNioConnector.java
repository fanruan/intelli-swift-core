package com.fr.swift.jdbc.rpc.nio;

import com.fr.swift.jdbc.decoder.NettyObjectDecoder;
import com.fr.swift.jdbc.decoder.SerializableDecoder;
import com.fr.swift.jdbc.encoder.NettyObjectEncoder;
import com.fr.swift.jdbc.encoder.SerializableEncoder;
import com.fr.swift.jdbc.rpc.AbstractRpcConnector;
import com.fr.swift.jdbc.rpc.JdbcRpcSelector;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcNioConnector extends AbstractRpcConnector {
    protected ConcurrentLinkedQueue<RpcRequest> sendQueueCache = new ConcurrentLinkedQueue<RpcRequest>();
    private SocketChannel channel;
    private JdbcRpcSelector selector;


    public RpcNioConnector(String address, SerializableEncoder encoder, SerializableDecoder decoder) {
        super(address);
        this.selector = new RpcNioSelector(encoder, decoder);
    }

    public RpcNioConnector(String address) {
        this(address, new NettyObjectEncoder(), new NettyObjectDecoder());
    }

    public RpcNioConnector(String host, int port) {
        this(host, port, new NettyObjectEncoder(), new NettyObjectDecoder());
    }

    public RpcNioConnector(String host, int port, SerializableEncoder encoder, SerializableDecoder decoder) {
        super(host, port);
        this.selector = new RpcNioSelector(encoder, decoder);
    }

    public RpcNioConnector(SocketChannel channel, JdbcRpcSelector selector) {
        this.channel = channel;
        this.selector = selector;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public RpcRequest getRequest() {
        return sendQueueCache.poll();
    }

    public boolean isNeedToSend() {
        return null != sendQueueCache.peek();
    }

    @Override
    public void notifySend() {
        selector.notifySend();
    }

    @Override
    public void start() {
        if (channel == null) {
            try {
                channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(host, port));
                channel.configureBlocking(false);
                while (!channel.isConnected()) {
                }
                selector.start();
                selector.register(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        try {
            channel.close();
            selector.stop();
        } catch (IOException e) {
        }
    }
}
