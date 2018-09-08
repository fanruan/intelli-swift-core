package com.fr.swift.jdbc.proxy.invoke;

import com.fr.swift.jdbc.proxy.JdbcConnector;
import com.fr.swift.jdbc.proxy.JdbcSelector;
import com.fr.swift.jdbc.thread.JdbcThreadFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yee
 * @date 2018/9/6
 */
public abstract class BaseSelector<T extends JdbcConnector> implements JdbcSelector<T> {
    private ExecutorService singleNotifyThread;

    public BaseSelector() {
    }

    @Override
    public void fireRpcResponse(final T connector, final RpcResponse object) {
        singleNotifyThread.submit(new Runnable() {
            @Override
            public void run() {
                connector.fireRpcResponse(object);
            }
        });
    }

    @Override
    public void fireRpcException(final T connector, final Exception object) {
        singleNotifyThread.submit(new Runnable() {
            @Override
            public void run() {
                connector.handlerException(object);
            }
        });
    }

    @Override
    public void handlerException(Exception e) {
        SwiftLoggers.getLogger().error(e);
    }

    @Override
    public void start() {
        singleNotifyThread = Executors.newSingleThreadExecutor(new JdbcThreadFactory.Builder().setName("swift-jdbc-notify-thread").build());
        setUpSelector();
    }

    /**
     * 启动selector
     */
    protected abstract void setUpSelector();

    @Override
    public void stop() {
        singleNotifyThread.shutdown();
        shutdownSelector();
    }

    /**
     * 停止selector
     */
    protected abstract void shutdownSelector();
}
