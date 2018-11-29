package com.fr.swift.jdbc.rpc.connection;

import com.fr.swift.jdbc.rpc.JdbcSelector;
import com.fr.swift.jdbc.rpc.invoke.BaseConnector;
import com.fr.swift.jdbc.rpc.selector.RpcNioSelector;
import com.fr.swift.jdbc.rpc.serializable.decoder.NettyObjectDecoder;
import com.fr.swift.jdbc.rpc.serializable.decoder.SerializableDecoder;
import com.fr.swift.jdbc.rpc.serializable.encoder.NettyObjectEncoder;
import com.fr.swift.jdbc.rpc.serializable.encoder.SerializableEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcNioConnector extends BaseConnector {
    private SocketChannel channel;
    private JdbcSelector selector;


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

    public RpcNioConnector(SocketChannel channel, JdbcSelector selector) {
        this.channel = channel;
        this.selector = selector;
    }

    public SocketChannel getChannel() {
        return channel;
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
