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

    public boolean containeEntityType(EntityType entityType) {
        synchronized (globalMetamodel) {
            return globalMetamodel.contain(entityType);
        }
    }

    public void addEntityType(EntityType entityType) {
        synchronized (globalMetamodel) {
            if (!containeEntityType(entityType)){
                globalMetamodel.build().addEntityType(entityType);
            }
        }
    }
}
