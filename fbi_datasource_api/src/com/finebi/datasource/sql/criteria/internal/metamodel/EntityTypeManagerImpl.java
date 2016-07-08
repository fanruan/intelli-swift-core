package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.api.metamodel.EntityTypeManagerFactory;
import com.finebi.datasource.api.metamodel.Metamodel;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;

/**
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public class EntityTypeManagerImpl implements EntityTypeManager {

    private AspireContext context;
    private CriteriaBuilder criteriaBuilder;
    private Metamodel metamodel;

    public EntityTypeManagerImpl(AspireContext context) {
        this.context = context;
        this.criteriaBuilder = new CriteriaBuilderImpl(context);

        metamodel = context.getMetamodel();
    }


    @Override
    public EntityTypeManagerFactory getEntityManagerFactory() {
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
    public Object getProperties(String name) {
        return context.getProperty(name);
    }

    @Override
    public boolean contains(EntityType entityType) {
        return false;
    }
}
