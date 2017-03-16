
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public abstract class AbstractSimplePredicate
        extends AbstractPredicateImpl
        implements Serializable {
    private static final List<Expression<Boolean>> NO_EXPRESSIONS = Collections.emptyList();

    public AbstractSimplePredicate(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder);
    }

    @Override
    public boolean isJunction() {
        return false;
    }

    @Override
    public BooleanOperator getOperator() {
        return BooleanOperator.AND;
    }

    @Override
    public final List<Expression<Boolean>> getExpressions() {
        return NO_EXPRESSIONS;
    }

    @Override
    public Object render(RenderingContext renderingContext) {
        return render(isNegated(), renderingContext);
    }

    @Override
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }

}
