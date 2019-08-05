package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;

/**
 * 创建FineIO Connector
 * 1. 插件存在则调用插件生成Connector
 * 2. 插件不存在则调用默认的Connector
 * 3. 监听插件状态，插件状态改变重新对Connector赋值
 *
 * @author yee
 * @date 2017/8/2
 */
public class ConnectorManager implements IConnectorManager {
    private volatile static ConnectorManager instance;
    private IConnectorManager manager;

    private ConnectorManager() {
        this.manager = SwiftConnectorManager.getInstance();
    }

    public static ConnectorManager getInstance() {
        if (null != instance) {
            return instance;
        }
        synchronized (ConnectorManager.class) {
            if (null != instance) {
                return instance;
            }
            instance = new ConnectorManager();
        }
        return instance;
    }


    public void setManager(IConnectorManager manager) {
        this.manager = manager;
    }

    @Override
    public Connector getConnector() {
        return manager.getConnector();
    }
}