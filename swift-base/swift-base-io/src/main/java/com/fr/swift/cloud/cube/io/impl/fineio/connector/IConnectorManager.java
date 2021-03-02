package com.fr.swift.cloud.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2019-07-10
 */
public interface IConnectorManager {
    /**
     * 获取Connector
     *
     * @return
     */
    Connector getConnector();
}
