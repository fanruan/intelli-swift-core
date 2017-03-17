package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.BooleanStaticAssertionPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BooleanStaticAssertionPredicateLiteralRender extends BasicLiteralRender<BooleanStaticAssertionPredicate> {
    public BooleanStaticAssertionPredicateLiteralRender(BooleanStaticAssertionPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        boolean isTrue = getDelegate().getAssertedValue();
        if (isNegated()) {
            isTrue = !isTrue;
        }
        return isTrue ? "1=1" : "0=1";
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}