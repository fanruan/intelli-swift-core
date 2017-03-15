package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.LiteralExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class LiteralExpressionEngineRender extends BasicEngineRender<LiteralExpression, Object> {
    private Object literal;

    public LiteralExpressionEngineRender(LiteralExpression delegate) {
        super(delegate);
        this.literal = getDelegate().getLiteral();
    }

    public Object render(RenderingContext renderingContext) {
        return literal;
    }

    @SuppressWarnings({"unchecked"})
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
