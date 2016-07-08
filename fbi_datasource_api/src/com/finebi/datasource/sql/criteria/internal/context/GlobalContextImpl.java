package com.finebi.datasource.sql.criteria.internal.context;

import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.Metamodel;

/**
 * This class created on 2016/7/7.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class GlobalContextImpl {
    private Metamodel globalMetamodel;
    private ContextProperty globalProperties;

    public GlobalContextImpl() {
        globalProperties = new ContextProperty();
    }

    public boolean containEntityType(EntityType entityType) {
        synchronized (globalMetamodel) {
            return globalMetamodel.contain(entityType);
        }
    }

    public void registerEntityType(EntityType entityType) {
        synchronized (globalMetamodel) {
            if (!containEntityType(entityType)) {
                globalMetamodel.build().addEntityType(entityType);
            }
        }
    }

    public void registerProperty(String name, Object property) {
        synchronized (globalProperties) {
            globalProperties.addProperty(name, property);
        }
    }
}
