package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.util.function.Function;

/**
 * @author yee
 * @date 2018-12-04
 */
public interface ConnectorProvider extends Function<FineIOConnectorConfig, Connector> {
    void change();
}
