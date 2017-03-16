package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.CoalesceExpression;
import com.finebi.datasource.sql.criteria.internal.expression.function.BasicFunctionExpression;

/**
 * This class created on 2016/7/6.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class CoalesceExpressionLiteralRender extends BasicLiteralRender<CoalesceExpression> {
    public CoalesceExpressionLiteralRender(CoalesceExpression delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        StringBuilder buffer = new StringBuilder( "coalesce(" );
        String sep = "";
        for ( Object expression :getDelegate().getExpressions() ) {
            buffer.append( sep )
                    .append( ( (Renderable) expression ).render( renderingContext ) );
            sep = ", ";
        }
        return buffer.append( ")" ).toString();
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }
}
