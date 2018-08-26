package com.fr.swift.jdbc.rpc.nio;

import com.fr.swift.jdbc.decoder.SerializableDecoder;
import com.fr.swift.jdbc.encoder.SerializableEncoder;
import com.fr.swift.jdbc.exception.NoCodecResponseException;
import com.fr.swift.jdbc.exception.RpcException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcRequest;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcSelector {
    private static final int READ_OP = SelectionKey.OP_READ;
    private static final int READ_WRITE_OP = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    private Selector selector;
    private LinkedList<Runnable> selectTasks = new LinkedList<Runnable>();
    private ConcurrentHashMap<SocketChannel, RpcConnector> connectorCache;
    private List<RpcConnector> connectors;
    private boolean started = false;
    private boolean stop = false;
    private SerializableEncoder encoder;
    private SerializableDecoder decoder;

    public RpcSelector(SerializableEncoder encoder, SerializableDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
        try {
            selector = Selector.open();
            connectorCache = new ConcurrentHashMap<SocketChannel, RpcConnector>();
            connectors = new CopyOnWriteArrayList<RpcConnector>();
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    private void addSelectTask(Runnable task) {
        selectTasks.offer(task);
    }

    private boolean hasTask() {
        Runnable peek = selectTasks.peek();
        return peek != null;
    }

    private void runSelectTasks() {
        Runnable peek = selectTasks.peek();
        while (peek != null) {
            peek = selectTasks.pop();
            peek.run();
            peek = selectTasks.peek();
        }
    }

    public void notifySend() {
        selector.wakeup();
    }

    public void register(final RpcConnector connector) {
        this.addSelectTask(new Runnable() {
            public void run() {
                try {
                    SelectionKey selectionKey = connector.getChannel().register(selector, READ_OP);
                    RpcSelector.this.initNewSocketChannel(connector.getChannel(), connector, selectionKey);
                } catch (Exception e) {
                    connector.handlerException(e);
                }
            }
        });
        this.notifySend();
    }

    private boolean doDispatchSelectionKey(SelectionKey selectionKey) {
        boolean result = false;
        try {
            if (selectionKey.isAcceptable()) {
                result = doAccept(selectionKey);
            }
            if (selectionKey.isReadable()) {
                result = doRead(selectionKey);
            }
            if (selectionKey.isWritable()) {
                result = doWrite(selectionKey);
            }
        } catch (Exception e) {
            handSelectionKeyException(selectionKey, e);
        }
        return result;
    }

    private void initNewSocketChannel(SocketChannel channel, RpcConnector connector, SelectionKey selectionKey) {
        connector.setSelectionKey(selectionKey);
        connectorCache.put(channel, connector);
        connectors.add(connector);
    }

    public void start() {
        if (!started) {
            new SelectionThread().start();
            started = true;
        }
    }

    public void stop() {
        stop = true;
    }

    private boolean doAccept(SelectionKey selectionKey) {
        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
        try {
            SocketChannel client = server.accept();
            if (client != null) {
                client.configureBlocking(false);
                RpcConnector connector = new RpcConnector(client, this);
                this.register(connector);
                connector.connect();
                return true;
            }
        } catch (Exception e) {
            handSelectionKeyException(selectionKey, e);
        }
        return false;
    }

    private boolean doRead(SelectionKey selectionKey) {
        boolean result = false;
        SocketChannel client = (SocketChannel) selectionKey.channel();
        RpcConnector connector = connectorCache.get(client);
        if (connector != null) {
            try {
                while (!stop) {
                    try {
                        Object object = decoder.decodeFromChannel(client);
                        this.fireRpcResponse(connector, object);
                        result = true;
                        break;
                    } catch (NoCodecResponseException e) {
                        break;
                    } catch (Exception e) {
                        handSelectionKeyException(selectionKey, e);
                        break;
                    }
                }
            } catch (Exception e) {
                handSelectionKeyException(selectionKey, e);
            }
        }
        return result;
    }

    private void fireRpcResponse(RpcConnector connector, Object object) {
        connector.fireRpcResponse(object);
    }

    private void fireRpcException(RpcConnector connector, Exception object) {
        connector.handlerException(object);
    }

    private void handSelectionKeyException(final SelectionKey selectionKey, Exception e) {
        SelectableChannel channel = selectionKey.channel();
        RpcConnector connector = connectorCache.get(channel);
        if (connector != null) {
            this.fireRpcException(connector, e);
        }
    }

    private boolean doWrite(SelectionKey selectionKey) {
        boolean result = false;
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        RpcConnector connector = connectorCache.get(channel);
        if (connector.isNeedToSend()) {
            try {
                while (connector.isNeedToSend()) {
                    RpcRequest rpc = connector.getRequest();
                    channel.write(encoder.encodeBuf(rpc));
                    result = true;
                }
                if (!connector.isNeedToSend()) {
                    selectionKey.interestOps(READ_OP);
                }
            } catch (Exception e) {
                handSelectionKeyException(selectionKey, e);
            }
        }
        return result;
    }

    private boolean checkSend() {
        boolean needSend = false;
        for (RpcConnector connector : connectors) {
            if (connector.isNeedToSend()) {
                SelectionKey selectionKey = connector.getChannel().keyFor(selector);
                selectionKey.interestOps(READ_WRITE_OP);
                needSend = true;
            }
        }
        return needSend;
    }

    private class SelectionThread extends Thread {
        @Override
        public void run() {
            while (!stop) {
                if (RpcSelector.this.hasTask()) {
                    RpcSelector.this.runSelectTasks();
                }
                boolean needSend = checkSend();
                try {
                    if (needSend) {
                        selector.selectNow();
                    } else {
                        selector.select();
                    }
                } catch (IOException e) {
                    SwiftLoggers.getLogger().error(e);
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    doDispatchSelectionKey(selectionKey);
                }
            }
        }
    }
}
