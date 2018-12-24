package com.fr.swift.config.service;

import com.fr.swift.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-20
 */
public interface SwiftFineIOConnectorService {
    FineIOConnectorConfig getCurrentConfig();

    void setCurrentConfig(FineIOConnectorConfig config);
}
