package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.ExistsPredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ExistsPredicateLiteralRender extends BasicLiteralRender<ExistsPredicate> {
    public ExistsPredicateLiteralRender(ExistsPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return (isNegated() ? "not " : "") + "exists "
                + ((Renderable) getDelegate().getSubquery()).render(renderingContext);
    }


    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}