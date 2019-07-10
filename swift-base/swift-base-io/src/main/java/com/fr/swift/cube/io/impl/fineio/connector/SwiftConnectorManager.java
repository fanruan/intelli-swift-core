package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;

/**
 * @author yee
 * @date 2019-07-10
 */
public class SwiftConnectorManager implements IConnectorManager {
    private volatile static SwiftConnectorManager instance;
    private SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
    private ConnectorProvider provider = SwiftContext.get().getBean(ConnectorProvider.class);
    private SwiftFineIOConnectorService fineIOConnectorService = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
    private volatile Connector connector;

    private SwiftConnectorManager() {
        pathService.registerPathChangeListener(new SwiftCubePathService.PathChangeListener() {
            @Override
            public void changed(String path) {
                connector = null;
            }
        });
    }

    public static SwiftConnectorManager getInstance() {
        if (null != instance) {
            return instance;
        }
        synchronized (SwiftConnectorManager.class) {
            if (null != instance) {
                return instance;
            }
            instance = new SwiftConnectorManager();
        }
        return instance;
    }


    @Override
    public Connector getConnector() {
        if (null == connector) {
            synchronized (this) {
                if (null == connector) {
                    connector = provider.apply(fineIOConnectorService.getCurrentConfig(SwiftFineIOConnectorService.Type.CONNECTOR));
                }
            }
        }
        return connector;
    }
}
