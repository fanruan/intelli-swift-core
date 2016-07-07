
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.ValueHandlerFactory;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * Represents a literal expression.
 *
 * @author Steve Ebersole
 */
public class LiteralExpression<T> extends ExpressionImpl<T> implements Serializable {
	private Object literal;
	@SuppressWarnings({ "unchecked" })
	public LiteralExpression(CriteriaBuilderImpl criteriaBuilder, T literal) {
		this( criteriaBuilder, (Class<T>) determineClass( literal ), literal );
	}

	private static Class determineClass(Object literal) {
		return literal == null ? null : literal.getClass();
	}

	public LiteralExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> type, T literal) {
		super( criteriaBuilder, type );
		this.literal = literal;
	}

	@SuppressWarnings({ "unchecked" })
	public T getLiteral() {
		return (T) literal;
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do
	}

	@SuppressWarnings({ "unchecked" })
	public Object render(RenderingContext renderingContext) {
		return delegateRender(renderingContext);
	}

	@SuppressWarnings({ "unchecked" })
	public Object renderProjection(RenderingContext renderingContext) {
		return delegateRenderProjection(renderingContext);
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	protected void resetJavaType(Class targetType) {
		super.resetJavaType( targetType );
		ValueHandlerFactory.ValueHandler valueHandler = getValueHandler();
		if ( valueHandler == null ) {
			valueHandler = ValueHandlerFactory.determineAppropriateHandler( targetType );
			forceConversion( valueHandler );
		}

		if ( valueHandler != null ) {
			literal = valueHandler.convert( literal );
		}
	}
	public  String delegateRender(RenderingContext renderingContext) {
		RenderExtended render = choseRender(renderingContext);
		return (String)render.render(renderingContext);
	}

	public String delegateRenderProjection(RenderingContext renderingContext) {
		RenderExtended render = choseRender(renderingContext);
		return (String)render.renderProjection(renderingContext);
	}

	protected RenderExtended choseRender(RenderingContext renderingContext) {
		return (RenderExtended) renderingContext.getRenderFactory().getLiteralExpressionLiteralRender(this, "default");
	}

}
