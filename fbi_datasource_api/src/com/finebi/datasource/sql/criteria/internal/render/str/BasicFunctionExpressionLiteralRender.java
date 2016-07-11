package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.function.BasicFunctionExpression;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BasicFunctionExpressionLiteralRender extends BasicLiteralRender<BasicFunctionExpression> {
    public BasicFunctionExpressionLiteralRender(BasicFunctionExpression delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return getDelegate(). getFunctionName() + "()";
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }
}