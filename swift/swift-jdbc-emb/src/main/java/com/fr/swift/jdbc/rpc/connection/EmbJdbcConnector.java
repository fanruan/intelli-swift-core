package com.fr.swift.jdbc.rpc.connection;


import com.fr.swift.jdbc.rpc.invoke.BaseConnector;
import com.fr.swift.jdbc.rpc.selector.EmbJdbcSelector;

/**
 * @author yee
 * @date 2018/9/8
 */
public class EmbJdbcConnector extends BaseConnector {
    private EmbJdbcSelector selector;

    public EmbJdbcConnector() {
        selector = new EmbJdbcSelector();
    }

    @Override
    public void notifySend() {
        selector.notifySend();
    }

    @Override
    public void start() {
        selector.register(this);
        selector.start();
    }

    @Override
    public void stop() {
        selector.stop();
    }

}
