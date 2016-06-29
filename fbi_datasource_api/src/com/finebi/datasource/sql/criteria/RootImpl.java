package com.finebi.datasource.sql.criteria;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;

import java.io.Serializable;


/**
 * Hibernate implementation of the JPA {@link Root} contract
 *
 * @author Steve Ebersole
 */
public class RootImpl<X> extends AbstractFromImpl<X,X> implements Root<X>, Serializable {
    public RootImpl(CriteriaBuilderImpl criteriaBuilder, PlainTable plainTable) {
        super(criteriaBuilder, plainTable);
    }
    @Override
    public EntityType<X> getModel() {
        return null;
    }
}
