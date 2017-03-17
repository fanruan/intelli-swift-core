package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since 4.0
 */
public class PathTypeExpressionRender extends BasicLiteralRender<PathTypeExpression> {
    public PathTypeExpressionRender(PathTypeExpression delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return "type(" + getDelegate().getPath().getPathIdentifier() + ")";
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
