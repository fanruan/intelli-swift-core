package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.ComparisonPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ComparisonPredicateLiteralRender extends BasicLiteralRender<ComparisonPredicate> {
    public ComparisonPredicateLiteralRender(ComparisonPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext)
                + getDelegate().getComparisonOperator(isNegated()).rendered()
                + ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext);
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}