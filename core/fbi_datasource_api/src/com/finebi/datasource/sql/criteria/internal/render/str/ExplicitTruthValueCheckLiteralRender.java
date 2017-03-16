package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.ExplicitTruthValueCheck;
import com.finebi.datasource.sql.criteria.internal.predicate.TruthValue;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ExplicitTruthValueCheckLiteralRender extends BasicLiteralRender<ExplicitTruthValueCheck> {
    public ExplicitTruthValueCheckLiteralRender(ExplicitTruthValueCheck delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return ((Renderable) getDelegate().getBooleanExpression()).render(renderingContext)
                + (isNegated() ? " <> " : " = ")
                + (getDelegate().getTruthValue() == TruthValue.TRUE ? "true" : "false");
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}