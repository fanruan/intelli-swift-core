package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.ValueHandlerFactory;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.LiteralExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class LiteralExpressionLiteralRender extends BasicLiteralRender<LiteralExpression> {
    private Object literal;
    public LiteralExpressionLiteralRender(LiteralExpression delegate) {
        super(delegate);
        this.literal =getDelegate().getLiteral();
    }
    public String render(RenderingContext renderingContext) {
        if ( ValueHandlerFactory.isNumeric( literal ) ) {
            return ValueHandlerFactory.determineAppropriateHandler( (Class) literal.getClass() ).render( literal );
        }

        // else...
        final String parameterName = renderingContext.registerLiteralParameterBinding( getDelegate().getLiteral(), getDelegate().getJavaType() );
        return ':' + parameterName;
    }

    @SuppressWarnings({ "unchecked" })
    public String renderProjection(RenderingContext renderingContext) {
        // some drivers/servers do not like parameters in the select clause
        final ValueHandlerFactory.ValueHandler handler =
                ValueHandlerFactory.determineAppropriateHandler( literal.getClass() );
        if ( ValueHandlerFactory.isCharacter( literal ) ) {
            return '\'' + handler.render( literal ) + '\'';
        }
        else {
            return handler.render( literal );
        }
    }
}
