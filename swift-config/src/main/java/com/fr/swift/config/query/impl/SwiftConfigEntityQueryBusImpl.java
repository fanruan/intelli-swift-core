package com.fr.swift.config.query.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.property.SwiftProperty;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-08-07
 */
public class SwiftConfigEntityQueryBusImpl extends SwiftHibernateConfigQueryBus<SwiftConfigEntity> implements SwiftConfigEntityQueryBus {
    public SwiftConfigEntityQueryBusImpl() {
        super(SwiftConfigEntity.class);
    }

    @Override
    public <T> T select(SwiftConfigConstants.Namespace namespace, Class<T> tClass, T defaultValue) {
        final String key = getKey(namespace);
        final SwiftConfigEntity select = select(key);
        try {
            return null != select ? new ObjectMapper().readValue(select.getConfigValue(), tClass) : defaultValue;
        } catch (IOException e) {
            return defaultValue;
        }
    }

    private String getKey(SwiftConfigConstants.Namespace namespace) {
        switch (namespace) {
            case SWIFT_CUBE_PATH:
            case FINE_IO_PACKAGE:
            case FINE_IO_CONNECTOR:
                return namespace.name() + "." + SwiftProperty.getProperty().getClusterId();
            default:
                return namespace.name();
        }
    }
}
