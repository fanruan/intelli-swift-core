package com.fr.swift.cloud.oss.connector;

import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorProcessor;

/**
 * @author yee
 * @date 2018-12-20
 */
public class OssConnectorProcessor implements ConnectorProcessor {
    @Override
    public Connector createConnector() {
        return new OssConnector();
    }

    @Override
    public int currentAPILevel() {
        return 0;
    }

    @Override
    public int layerIndex() {
        return 0;
    }
}
