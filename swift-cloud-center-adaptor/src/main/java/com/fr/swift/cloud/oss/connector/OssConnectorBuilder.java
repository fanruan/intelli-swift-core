package com.fr.swift.cloud.oss.connector;

import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.annotation.ConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.BaseConnectorBuilder;

/**
 * @author yee
 * @date 2018-12-20
 */
@ConnectorBuilder(OssConnectorConfig.OSS)
public class OssConnectorBuilder extends BaseConnectorBuilder {
    @Override
    public Connector build() {
        return new OssConnector();
    }
}
