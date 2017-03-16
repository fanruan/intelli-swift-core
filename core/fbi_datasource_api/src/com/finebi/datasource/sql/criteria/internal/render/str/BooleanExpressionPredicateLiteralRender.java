package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.BooleanExpressionPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BooleanExpressionPredicateLiteralRender extends BasicLiteralRender<BooleanExpressionPredicate> {
    public BooleanExpressionPredicateLiteralRender(BooleanExpressionPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return ((Renderable) getDelegate().getExpression()).render(renderingContext).toString();
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}