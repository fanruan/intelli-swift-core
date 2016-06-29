package com.finebi.datasource.sql.criteria;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */

import java.io.Serializable;

/**
 * All nodes in a criteria query tree will generally need access to the {@link CriteriaBuilderImpl} from which they
 * come.  This base class provides convenient, consistent support for that.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractNode implements Serializable {
    private final CriteriaBuilderImpl criteriaBuilder;

    public AbstractNode(CriteriaBuilderImpl criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    /**
     * Provides access to the underlying {@link CriteriaBuilderImpl}.
     *
     * @return The underlying {@link CriteriaBuilderImpl} instance.
     */
    public CriteriaBuilderImpl criteriaBuilder() {
        return criteriaBuilder;
    }
}
