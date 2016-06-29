
package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.criteria.From;
import com.finebi.datasource.api.criteria.Join;
import com.finebi.datasource.api.criteria.JoinType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.api.metamodel.SingularAttribute;

import java.io.Serializable;
import java.util.Set;

/**
 * Convenience base class for various {@link From} implementations.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractFromImpl<Z, X>
        extends AbstractPathImpl<X>
        implements From<Z, X>, Serializable {

    public static final JoinType DEFAULT_JOIN_TYPE = JoinType.INNER;

    private Set<Join<X, ?>> joins;


    public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, PlainTable plainTable) {
        super(criteriaBuilder, plainTable);
    }


    @Override
    public Set<Join<X, ?>> getJoins() {
        return null;
    }

    @Override
    public boolean isCorrelated() {
        return false;
    }

    @Override
    public From<Z, X> getCorrelationParent() {
        return null;
    }

    @Override
    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute) {
        return null;
    }

    @Override
    public Join join(PlainTable attribute) {
        return null;
    }

    @Override
    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
        return null;
    }

    @Override
    public <X1, Y> Join<X1, Y> join(String attributeName) {
        return null;
    }

    @Override
    public <X1, Y> Join<X1, Y> join(String attributeName, JoinType jt) {
        return null;
    }
}
