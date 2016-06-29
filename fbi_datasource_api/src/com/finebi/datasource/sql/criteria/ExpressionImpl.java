
package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.api.metamodel.PlainTable;

import java.io.Serializable;
import java.util.Collection;

/**
 * Models an expression in the criteria query language.
 *
 * @author Steve Ebersole
 */
public abstract class ExpressionImpl<T>
		extends SelectionImpl<T>
		implements Expression<T>, Serializable {
	public ExpressionImpl(CriteriaBuilderImpl criteriaBuilder, PlainTable plainTable) {
		super( criteriaBuilder, plainTable );
	}

    @Override
    public Predicate isNull() {
        return null;
    }

    @Override
    public Predicate isNotNull() {
        return null;
    }

    @Override
    public Predicate in(Object... values) {
        return null;
    }

    @Override
    public Predicate in(Expression<?>... values) {
        return null;
    }

    @Override
    public Predicate in(Collection<?> values) {
        return null;
    }

    @Override
    public Predicate in(Expression<Collection<?>> values) {
        return null;
    }

    @Override
    public <X> Expression<X> as(Class<X> type) {
        return null;
    }
}
