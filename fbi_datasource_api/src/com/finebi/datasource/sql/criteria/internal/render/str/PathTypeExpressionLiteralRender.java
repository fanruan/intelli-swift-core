package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class PathTypeExpressionLiteralRender extends BasicLiteralRender<PathTypeExpression> {
    public PathTypeExpressionLiteralRender(PathTypeExpression delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        RenderExtended render = (RenderExtended) renderingContext.getRenderFactory().getPathTypeExpressionRender(getDelegate(), "d");
        render.render(renderingContext);
        return render.getRenderResult().toString();
    }
    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
