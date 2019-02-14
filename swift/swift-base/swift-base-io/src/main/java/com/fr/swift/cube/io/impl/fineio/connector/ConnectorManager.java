package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;

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
    private SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
    private ConnectorProvider provider = SwiftContext.get().getBean(ConnectorProvider.class);
    private SwiftFineIOConnectorService fineIOConnectorService = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
    private volatile Connector connector;

    private ConnectorManager() {
        pathService.registerPathChangeListener(new SwiftCubePathService.PathChangeListener() {
            @Override
            public void changed(String path) {
                connector = null;
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



    public Connector getConnector() {
        if (null == connector) {
            synchronized (this) {
                if (null == connector) {
                    connector = provider.apply(fineIOConnectorService.getCurrentConfig());
                }
            }
        }
        return connector;
    }
}