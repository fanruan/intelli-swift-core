package com.fr.swift.config.service;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-20
 */
public interface SwiftFineIOConnectorService {

    FineIOConnectorConfig getCurrentConfig(SwiftConfigConstants.Namespace type);

    void setCurrentConfig(FineIOConnectorConfig config, SwiftConfigConstants.Namespace namespace);

    void registerListener(ConfChangeListener listener, SwiftConfigConstants.Namespace namespace);

    interface ConfChangeListener {
        void change(FineIOConnectorConfig change);
    }
}
