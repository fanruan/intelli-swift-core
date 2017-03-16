package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.NullnessPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class NullnessPredicateLiteralRender extends BasicLiteralRender<NullnessPredicate> {
    public NullnessPredicateLiteralRender(NullnessPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return ((Renderable) getDelegate().getOperand()).render(renderingContext) + check(isNegated());
    }

    private String check(boolean negated) {
        return negated ? " is not null" : " is null";
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}