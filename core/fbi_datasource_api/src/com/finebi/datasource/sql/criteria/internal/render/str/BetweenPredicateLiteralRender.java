package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.BetweenPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BetweenPredicateLiteralRender extends BasicLiteralRender<BetweenPredicate> {
    public BetweenPredicateLiteralRender(BetweenPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        final String operator = isNegated() ? " not between " : " between ";
        return ((Renderable) getDelegate().getExpression()).render(renderingContext)
                + operator
                + ((Renderable) getDelegate().getLowerBound()).render(renderingContext)
                + " and "
                + ((Renderable) getDelegate().getUpperBound()).render(renderingContext);
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}