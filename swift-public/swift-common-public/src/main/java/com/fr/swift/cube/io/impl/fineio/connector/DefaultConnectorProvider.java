package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.structure.Pair;

/**
 * @author yee
 * @date 2018-12-04
 */
@SwiftBean
public class DefaultConnectorProvider implements ConnectorProvider {

    private static Connector connector;

    @Override
    public SwiftCubePathService.PathChangeListener change() {
        return new SwiftCubePathService.PathChangeListener() {
            @Override
            public void changed(String path) {
                connector = null;
            }
        };
    }

    @Override
    public Connector apply(Pair<String, Boolean> p) {
        if (connector != null) {
            return connector;
        }
        connector = createConnector(p.getKey(), p.getValue());
        return connector;
    }

    private Connector createConnector(String path, boolean zip) {
        if (zip) {
            return Lz4Connector.newInstance(path);
        }
        return FileConnector.newInstance(path);
    }
}
