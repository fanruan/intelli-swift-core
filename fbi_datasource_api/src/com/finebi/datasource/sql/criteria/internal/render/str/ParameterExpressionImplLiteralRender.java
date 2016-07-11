package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.ParameterExpressionImpl;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class ParameterExpressionImplLiteralRender extends BasicLiteralRender<ParameterExpressionImpl> {
    public ParameterExpressionImplLiteralRender(ParameterExpressionImpl delegate) {
        super(delegate);
    }
    public String render(RenderingContext renderingContext) {
        final ExplicitParameterInfo parameterInfo = renderingContext.registerExplicitParameter( getDelegate() );
        return parameterInfo.render();
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render( renderingContext );
    }

}
