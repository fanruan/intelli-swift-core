package com.fr.swift.config.service;

import com.fr.swift.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-20
 */
public interface SwiftFineIOConnectorService {

    FineIOConnectorConfig getCurrentConfig(Type type);

    void setCurrentConfig(FineIOConnectorConfig config, Type type);

    void registerListener(ConfChangeListener listener, Type type);

    interface ConfChangeListener {
        void change(FineIOConnectorConfig change);
    }

    enum Type {
        //
        CONNECTOR, PACKAGE
    }
}
