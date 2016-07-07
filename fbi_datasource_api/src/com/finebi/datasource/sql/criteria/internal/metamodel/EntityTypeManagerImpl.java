package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.metamodel.EntityManagerFactory;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.api.metamodel.Metamodel;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.fr.bi.stable.utils.program.BICollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public class EntityTypeManagerImpl implements EntityTypeManager {

    private AspireContext context;
    private boolean isAvailable;
    private CriteriaBuilder criteriaBuilder;
    private Metamodel metamodel;
    private Map<String, Object> properties;

    public EntityTypeManagerImpl(AspireContext context) {
        this.context = context;
        this.criteriaBuilder = new CriteriaBuilderImpl(context);
        properties = new ConcurrentHashMap<String, Object>();
    }

    public Builder getBuilder() {
        return new Builder();
    }

    public class Builder {
        public void addProperty(String name, Object value) {
            properties.put(name, value);
        }

        public void setMetamodel(Metamodel metamodel) {
            EntityTypeManagerImpl.this.metamodel = metamodel;
        }
    }

    @Override
    public void flush() {
    }


    @Override
    public void clear() {

    }


    @Override
    public Map<String, Object> getProperties() {
        return BICollectionUtils.unmodifiedCollection(properties);
    }


    @Override
    public void close() {
        this.isAvailable = false;
    }

    @Override
    public boolean isOpen() {
        return this.isAvailable;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return null;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    @Override
    public Metamodel getMetamodel() {
        return metamodel;
    }

    @Override
    public void refresh(EntityType entityType) {

    }

    @Override
    public boolean contains(EntityType entityType) {
        return false;
    }
}
