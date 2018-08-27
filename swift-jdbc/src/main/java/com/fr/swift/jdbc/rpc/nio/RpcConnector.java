package com.fr.swift.jdbc.rpc.nio;

import com.fr.swift.jdbc.decoder.NettyObjectDecoder;
import com.fr.swift.jdbc.decoder.SerializableDecoder;
import com.fr.swift.jdbc.encoder.NettyObjectEncoder;
import com.fr.swift.jdbc.encoder.SerializableEncoder;
import com.fr.swift.jdbc.exception.RpcException;
import com.fr.swift.jdbc.rpc.RpcExecutor;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcConnector {
    protected ConcurrentLinkedQueue<RpcRequest> sendQueueCache = new ConcurrentLinkedQueue<RpcRequest>();
    private SocketChannel channel;
    private RpcSelector selector;
    private SelectionKey selectionKey;
    private String host = "localhost";
    private int port = 7000;
    private List<RpcExecutor> rpcExecutors;


    public RpcConnector(String address, SerializableEncoder encoder, SerializableDecoder decoder) {
        this.selector = new RpcSelector(encoder, decoder);
        String[] array = address.split(":");
        host = array[0];
        if (array.length > 1) {
            port = Integer.parseInt(array[1]);
        }
        rpcExecutors = new ArrayList<RpcExecutor>();
    }

    public RpcConnector(String address) {
        this(address, new NettyObjectEncoder(), new NettyObjectDecoder());
    }

    public RpcConnector(String host, int port) {
        this(host, port, new NettyObjectEncoder(), new NettyObjectDecoder());
    }

    public RpcConnector(String host, int port, SerializableEncoder encoder, SerializableDecoder decoder) {
        this.selector = new RpcSelector(encoder, decoder);
        this.host = host;
        this.port = port == -1 ? 7000 : port;
        rpcExecutors = new ArrayList<RpcExecutor>();
    }

    public RpcConnector(SocketChannel channel, RpcSelector selector) {
        this.channel = channel;
        this.selector = selector;
        rpcExecutors = new ArrayList<RpcExecutor>();
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public RpcSelector getSelector() {
        return selector;
    }

    public void setSelector(RpcSelector selector) {
        this.selector = selector;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void connect() {
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

    public RpcRequest getRequest() {
        return sendQueueCache.poll();
    }

    public boolean isNeedToSend() {
        return null != sendQueueCache.peek();
    }

    public void fireRpcResponse(Object object) {
        for (RpcExecutor rpcExecutor : rpcExecutors) {
            rpcExecutor.onRpcMessage((RpcResponse) object);
        }
    }

    public void registerExecutor(RpcExecutor executor) {
        rpcExecutors.add(executor);
    }

    public void disConnect() {
        try {
            channel.close();
            selector.stop();
        } catch (IOException e) {
        }
    }

    public boolean sendRpcObject(RpcRequest rpc, int timeout) {
        int cost = 0;
        while (!sendQueueCache.offer(rpc)) {
            cost += 3;
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
            if (timeout > 0 && cost > timeout) {
                throw new RpcException("request time out");
            }
        }
        this.notifySend();
        return true;
    }

    private void notifySend() {
        selector.notifySend();
    }

    public void handlerException(Exception e) {
        SwiftLoggers.getLogger().error(e);
        disConnect();
    }
}
