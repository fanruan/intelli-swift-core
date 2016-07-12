package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.NegatedPredicateWrapper;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class NegatedPredicateLiteralRender extends BasicLiteralRender<NegatedPredicateWrapper> {
    public NegatedPredicateLiteralRender(NegatedPredicateWrapper delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        if (getDelegate().isJunction()) {
            return CompoundPredicateLiteralRender.render(getDelegate(), renderingContext);
        } else {
            return getDelegate().getPredicate().render(isNegated(), renderingContext).toString();
        }
    }


    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}