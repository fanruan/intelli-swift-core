package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.BooleanAssertionPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BooleanAssertionPredicateLiteralRender extends BasicLiteralRender<BooleanAssertionPredicate> {
    public BooleanAssertionPredicateLiteralRender(BooleanAssertionPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        final String operator = isNegated() ? " <> " : " = ";
        final String assertionLiteral = getDelegate().getAssertedValue() ? "true" : "false";

        return ((Renderable) getDelegate().getExpression()).render(renderingContext)
                + operator
                + assertionLiteral;
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}