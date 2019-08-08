package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;

/**
 * @author yee
 * @date 2019-07-10
 */
public class SwiftConnectorManager implements IConnectorManager {
    private volatile static SwiftConnectorManager instance;
    private SwiftConfig swiftConfig = SwiftContext.get().getBean(SwiftConfig.class);
    private ConnectorProvider provider = SwiftContext.get().getBean(ConnectorProvider.class);
    private volatile Connector connector;

    private SwiftConnectorManager() {
        swiftConfig.command(SwiftConfigEntity.class).addSaveOrUpdateListener(new SwiftConfigCommandBus.SaveOrUpdateListener<SwiftConfigEntity>() {
            @Override
            public void saveOrUpdate(SwiftConfigEntity entity) {
                if (entity.getConfigKey().contains(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH.name())) {
                    connector = null;
                }
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
                    SwiftConfigEntityQueryBus query = (SwiftConfigEntityQueryBus) swiftConfig.query(SwiftConfigEntity.class);
                    connector = provider.apply(query.select(SwiftConfigConstants.Namespace.FINE_IO_CONNECTOR, FineIOConnectorConfig.class, null));
                }
            }
        }
        return connector;
    }
}
