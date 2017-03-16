package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.ConcatExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class ConcatExpressionLiteralRender extends BasicLiteralRender<ConcatExpression> {
    public ConcatExpressionLiteralRender(ConcatExpression delegate){
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        return ( (Renderable)getDelegate(). getString1() ).render( renderingContext )
                + " || "
                + ( (Renderable)getDelegate(). getString2() ).render( renderingContext );
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }
}
