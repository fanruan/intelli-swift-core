
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.From;
import com.finebi.datasource.api.criteria.JoinType;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.sql.criteria.internal.*;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.AbstractPredicateImpl;

import java.io.Serializable;

/**
 * Convenience base class for various {@link com.finebi.datasource.api.criteria.Join} implementations.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractJoinImpl<Z, X>
        extends AbstractFromImpl<Z, X>
        implements JoinImplementor<Z, X>, Serializable {

    private final Attribute<? super Z, ?> joinAttribute = null;
    private final JoinType joinType;
    private final EntityType<X> rightEntityType;
    private Predicate suppliedJoinCondition;

    public AbstractJoinImpl(
            CriteriaBuilderImpl criteriaBuilder,
            PathSource<Z> pathSource,
            EntityType<X> rightEntityType,
            JoinType joinType) {
        this(criteriaBuilder, rightEntityType.getJavaType(), pathSource, rightEntityType, joinType);
    }


    public AbstractJoinImpl(
            CriteriaBuilderImpl criteriaBuilder,
            Class<X> javaType,
            PathSource<Z> pathSource,
            EntityType<X> rightEntityType,
            JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource);
        this.rightEntityType = rightEntityType;
        this.joinType = joinType;
    }

    @Override
    public Attribute<? super Z, ?> getAttribute() {
        return joinAttribute;
    }

    @Override
    public JoinType getJoinType() {
        return joinType;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public From<?, Z> getParent() {
        // this cast should be ok by virtue of our constructors...
        return (From<?, Z>) getPathSource();
    }

    @Override
    public String renderTableExpression(RenderingContext renderingContext) {
        prepareAlias(renderingContext);
        ((FromImplementor) getParent()).prepareAlias(renderingContext);
        StringBuilder tableExpression = new StringBuilder();
        tableExpression.append(rightEntityType.getName())
                .append(" as ")
                .append(getAlias());
        if (suppliedJoinCondition != null) {
            tableExpression.append(" with ")
                    .append(((AbstractPredicateImpl) suppliedJoinCondition).render(renderingContext));
        }
        return tableExpression.toString();
    }

    @Override
    public JoinImplementor<Z, X> correlateTo(CriteriaSubqueryImpl subquery) {
        return (JoinImplementor<Z, X>) super.correlateTo(subquery);
    }

    @Override
    public JoinImplementor<Z, X> on(Predicate... restrictions) {
        // no matter what, a call to this method replaces any previously set values...
        this.suppliedJoinCondition = null;

        if (restrictions != null && restrictions.length > 0) {
            this.suppliedJoinCondition = criteriaBuilder().and(restrictions);
        }

        return this;
    }

    @Override
    public JoinImplementor<Z, X> on(Expression<Boolean> restriction) {
        this.suppliedJoinCondition = criteriaBuilder().wrap(restriction);
        return this;
    }

    @Override
    public Predicate getOn() {
        return suppliedJoinCondition;
    }
}
