package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.plugin.context.PluginContext;
import com.fr.plugin.manage.PluginFilter;
import com.fr.plugin.observer.PluginEvent;
import com.fr.plugin.observer.PluginEventListener;
import com.fr.plugin.observer.PluginEventType;
import com.fr.plugin.observer.PluginListenerRegistration;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.plugin.ExtraClassManagerProvider;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.context.SwiftContext;
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
    private SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);

    private ConnectorManager() {
        listenPlugin();
        pathService.registerPathChangeListener(new SwiftCubePathService.PathChangeListener() {
            @Override
            public void changed(String path) {
                connector = null;
                connector = createConnector();
            }
        });
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
        return createConnector();
    }

    private Connector createConnector() {
        synchronized (this) {
            if (null != connector) {
                return connector;
            }
            ExtraClassManagerProvider pluginProvider = StableFactory.getMarkedObject(ExtraClassManagerProvider.XML_TAG, ExtraClassManagerProvider.class);
            if (null == pluginProvider) {
                connector = createSwiftConnector();
                return connector;
            }
            ConnectorProcessor connectorProcessor = pluginProvider.getSingle(ConnectorProcessor.MARK_STRING);
            if (null == connectorProcessor) {
                connector = createSwiftConnector();
                return connector;
            }
            connector = connectorProcessor.createConnector();
            if (null == connector) {
                connector = createSwiftConnector();
            }
            return connector;
        }
    }

    private Connector createSwiftConnector() {
        SwiftFineIOConnectorService service = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
        FineIOConnectorConfig config = service.getCurrentConfig();
        return SwiftConnectorCreator.create(config);
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
