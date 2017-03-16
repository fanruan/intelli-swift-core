package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.SimpleCaseExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class SimpleCaseExpressionLiteralRender extends BasicLiteralRender<SimpleCaseExpression> {
    public SimpleCaseExpressionLiteralRender(SimpleCaseExpression delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        return "";
    }

    public String renderProjection(RenderingContext renderingContext) {
        return "";
    }

}
