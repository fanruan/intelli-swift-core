
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Models a <tt>BETWEEN</tt> {@link com.finebi.datasource.api.criteria.Predicate}.
 *
 * @author Steve Ebersole
 */
public class BetweenPredicate<Y>
        extends AbstractSimplePredicate
        implements Serializable {
    private final Expression<? extends Y> expression;
    private final Expression<? extends Y> lowerBound;
    private final Expression<? extends Y> upperBound;

    public BetweenPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<? extends Y> expression,
            Y lowerBound,
            Y upperBound) {
        this(
                criteriaBuilder,
                expression,
                criteriaBuilder.literal(lowerBound),
                criteriaBuilder.literal(upperBound)
        );
    }

    public BetweenPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<? extends Y> expression,
            Expression<? extends Y> lowerBound,
            Expression<? extends Y> upperBound) {
        super(criteriaBuilder);
        this.expression = expression;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Expression<? extends Y> getExpression() {
        return expression;
    }

    public Expression<? extends Y> getLowerBound() {
        return lowerBound;
    }

    public Expression<? extends Y> getUpperBound() {
        return upperBound;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(getExpression(), registry);
        Helper.possibleParameter(getLowerBound(), registry);
        Helper.possibleParameter(getUpperBound(), registry);
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getBetweenPredicateRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);

    }
}
