package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.FineIO;
import com.fineio.storage.Connector;
import com.fr.plugin.context.PluginContext;
import com.fr.plugin.manage.PluginFilter;
import com.fr.plugin.observer.PluginEvent;
import com.fr.plugin.observer.PluginEventListener;
import com.fr.plugin.observer.PluginEventType;
import com.fr.plugin.observer.PluginListenerRegistration;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.plugin.ExtraClassManagerProvider;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

/**
 * 创建FineIO Connector
 * 1. 插件存在则调用插件生成Connector
 * 2. 插件不存在则调用默认的Connector
 * 3. 监听插件状态，插件状态改变重新对Connector赋值
 *
 * @author yee
 * @date 2017/8/2
 */
public class ConnectorManager {
    private volatile static ConnectorManager instance;
    private static Connector connector;
    private static final long CACHE_TIME = 24 * 60 * 60 * 1000L;

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

    private ConnectorManager() {
        listenPlugin();
    }

    private void listenPlugin() {
        PluginFilter filter = new PluginFilter() {
            @Override
            public boolean accept(PluginContext context) {
                return context.contain(ConnectorProcessor.MARK_STRING);
            }
        };
        ConnectorPluginListener listener = new ConnectorPluginListener();
        PluginListenerRegistration.getInstance().listen(PluginEventType.AfterActive, listener, filter);
        PluginListenerRegistration.getInstance().listen(PluginEventType.AfterStop, listener, filter);
        PluginListenerRegistration.getInstance().listen(PluginEventType.AfterUnload, listener, filter);
    }

    public Connector getConnector() {
        if (null != connector) {
            return connector;
        }
        synchronized (this) {
            if (null != connector) {
                return connector;
            }
//            long cacheTimer = ZipUriConf.getInstance().getCacheTimer();
            FineIO.setMemoryCheckSchedule(CACHE_TIME);
            ExtraClassManagerProvider pluginProvider = StableFactory.getMarkedObject(ExtraClassManagerProvider.XML_TAG, ExtraClassManagerProvider.class);
            if (null == pluginProvider) {
                connector = ZipConnector.newInstance();
                return connector;
            }
            ConnectorProcessor connectorProcessor = pluginProvider.getSingle(ConnectorProcessor.MARK_STRING);
            if (null == connectorProcessor) {
                connector = ZipConnector.newInstance();
                return connector;
            }
            connector = connectorProcessor.createConnector();
            if (null == connector) {
                connector = ZipConnector.newInstance();
            }
            return connector;
        }
    }

    protected class ConnectorPluginListener extends PluginEventListener {
        @Override
        public void on(PluginEvent pluginEvent) {
            try {
                connector = null;
                connector = ConnectorManager.this.getConnector();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                Crasher.crash(e);
            }
        }
    }
}
