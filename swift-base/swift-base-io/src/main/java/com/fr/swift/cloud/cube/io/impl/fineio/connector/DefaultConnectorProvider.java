package com.fr.swift.cloud.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-04
 */
@SwiftBean
public class DefaultConnectorProvider implements ConnectorProvider {

    private static Connector connector;

    @Override
    public void change() {
        connector = null;
    }

    @Override
    public Connector apply(FineIOConnectorConfig config) {
        if (connector != null) {
            return connector;
        }
        connector = SwiftConnectorCreator.create(config);
        return connector;
    }
}
