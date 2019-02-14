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
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

/**
 * @author yee
 * @date 2018-12-04
 */
@SwiftBean
public class FRConnectorProvider implements ConnectorProvider {

    private static Connector connector;

    public FRConnectorProvider() {
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

    @Override
    public Connector apply(FineIOConnectorConfig p) {
        if (null != connector) {
            return connector;
        }
        ExtraClassManagerProvider pluginProvider = StableFactory.getMarkedObject(ExtraClassManagerProvider.XML_TAG, ExtraClassManagerProvider.class);
        if (null == pluginProvider) {
            connector = SwiftConnectorCreator.create(p);
            return connector;
        }
        ConnectorProcessor connectorProcessor = pluginProvider.getSingle(ConnectorProcessor.MARK_STRING);
        if (null == connectorProcessor) {
            connector = SwiftConnectorCreator.create(p);
            return connector;
        }
        connector = connectorProcessor.createConnector();
        if (null == connector) {
            connector = SwiftConnectorCreator.create(p);
        }
        return connector;
    }


    @Override
    public void change() {
        connector = null;
    }

    protected class ConnectorPluginListener extends PluginEventListener {
        @Override
        public void on(PluginEvent pluginEvent) {
            try {
                connector = null;
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                Crasher.crash(e);
            }
        }
    }
}
