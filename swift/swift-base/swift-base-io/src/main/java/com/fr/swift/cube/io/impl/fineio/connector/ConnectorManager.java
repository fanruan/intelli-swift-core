package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.swift.structure.Pair;

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
    private SwiftZipService zipConfig = SwiftContext.get().getBean(SwiftZipService.class);
    private ConnectorProvider provider = SwiftContext.get().getBean(ConnectorProvider.class);

    private ConnectorManager() {
        pathService.registerPathChangeListener(provider.change());
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
        String path = pathService.getSwiftPath();
        synchronized (this) {
            boolean useZip = zipConfig.isZip();
            return provider.apply(Pair.of(path, useZip));
        }
    }
}
