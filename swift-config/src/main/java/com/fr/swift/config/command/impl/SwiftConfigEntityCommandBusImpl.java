package com.fr.swift.config.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.command.SwiftConfigEntityCommandBus;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;

/**
 * @author yee
 * @date 2019-08-07
 */
public class SwiftConfigEntityCommandBusImpl extends SwiftHibernateConfigCommandBus<SwiftConfigEntity> implements SwiftConfigEntityCommandBus {
    public SwiftConfigEntityCommandBusImpl() {
        super(SwiftConfigEntity.class);
    }

    @Override
    public void merge(SwiftConfigConstants.Namespace namespace, Object obj) {
        final String key = getKey(namespace);
        try {
            final SwiftConfigEntity configEntity = new SwiftConfigEntity(key, new ObjectMapper().writeValueAsString(obj));
            merge(configEntity);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger().error("merge config key {} error", key, e);
        }
    }

    @Override
    public void delete(SwiftConfigConstants.Namespace key) {
        delete(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("configKey", getKey(key))));
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
