package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.SubqueryComparisonModifierExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class SubqueryComparisonModifierExpressionLiteralRender extends BasicLiteralRender<SubqueryComparisonModifierExpression> {
    public SubqueryComparisonModifierExpressionLiteralRender(SubqueryComparisonModifierExpression delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        return getDelegate().getModifier().rendered() + ( (Renderable)getDelegate().getSubquery() ).render( renderingContext );
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }
}
