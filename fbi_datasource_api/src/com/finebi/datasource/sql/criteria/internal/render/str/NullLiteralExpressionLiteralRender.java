package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.NullLiteralExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class NullLiteralExpressionLiteralRender extends BasicLiteralRender<NullLiteralExpression> {
    public NullLiteralExpressionLiteralRender(NullLiteralExpression delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        return "null";
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }
}
