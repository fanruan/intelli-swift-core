package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.NullifExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class NullifExpressionLiteralRender extends BasicLiteralRender<NullifExpression> {
    public NullifExpressionLiteralRender(NullifExpression delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        return "nullif("
                + ( (Renderable) getDelegate().getPrimaryExpression() ).render( renderingContext )
                + ','
                + ( (Renderable)getDelegate().getSecondaryExpression() ).render( renderingContext )
                + ")";
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }
}
