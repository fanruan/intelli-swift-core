package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.ConfigInputUtil;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.impl.fineio.connector.config.CommonConnectorConfig;
import com.fr.swift.log.SwiftLoggers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yee
 * @date 2019-07-10
 */
public class SwiftConnectorManager implements IConnectorManager {
    private volatile static SwiftConnectorManager instance;
    private SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
    private ConnectorProvider provider = SwiftContext.get().getBean(ConnectorProvider.class);
    private volatile Connector connector;
    private static String FINEIO_TPYE = "LZ4";

    private SwiftConnectorManager() {
        pathService.registerPathChangeListener(path -> connector = null);
    }

    public static SwiftConnectorManager getInstance() {
        if (null != instance) {
            return instance;
        }
        synchronized (SwiftConnectorManager.class) {
            if (null != instance) {
                return instance;
            }
            try (InputStream in = ConfigInputUtil.getConfigInputStream("public.conf")) {
                Properties properties = new Properties();
                properties.load(in);
                FINEIO_TPYE = properties.getProperty("fineio.type");
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e);
            }

            instance = new SwiftConnectorManager();
        }
        return instance;
    }

    public Connector getConnector() {
        if (null == connector) {
            synchronized (this) {
                if (null == connector) {
                    connector = provider.apply(new CommonConnectorConfig(CommonConnectorType.valueOf(FINEIO_TPYE.toUpperCase())));
                }
            }
        }
        return connector;
    }
}
