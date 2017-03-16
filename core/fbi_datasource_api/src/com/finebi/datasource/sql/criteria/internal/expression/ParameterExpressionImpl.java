
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.sql.criteria.internal.important.QueryParameter;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.Type;
import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import com.finebi.datasource.api.criteria.ParameterExpression;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Defines a parameter specification, or the information about a parameter (where it occurs, what is
 * its type, etc).
 *
 * @author Steve Ebersole
 */
public class ParameterExpressionImpl<T>
		extends ExpressionImpl<T>
		implements ParameterExpression<T>, QueryParameter<T>, Serializable {
	private final String name;
	private final Integer position;
	private final Type expectedType;

	public ParameterExpressionImpl(
			CriteriaBuilderImpl criteriaBuilder,
			Class<T> javaType,
			String name,
			Type expectedType) {
		super( criteriaBuilder, javaType );
		this.name = name;
		this.position = null;
		this.expectedType = expectedType;
	}

	public ParameterExpressionImpl(
			CriteriaBuilderImpl criteriaBuilder,
			Class<T> javaType,
			Integer position,
			Type expectedType) {
		super( criteriaBuilder, javaType );
		this.name = null;
		this.position = position;
		this.expectedType = expectedType;
	}

	public ParameterExpressionImpl(
			CriteriaBuilderImpl criteriaBuilder,
			Class<T> javaType,
			Type expectedType) {
		super( criteriaBuilder, javaType );
		this.name = null;
		this.position = null;
		this.expectedType = expectedType;
	}

	public String getName() {
		return name;
	}

	public Integer getPosition() {
		return position;
	}

	public Class<T> getParameterType() {
		return getJavaType();
	}

	public void registerParameters(ParameterRegistry registry) {
		registry.registerParameter( this );
	}

	public String render(RenderingContext renderingContext) {
		return (String)delegateRender(renderingContext);
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
	public Object delegateRender(RenderingContext renderingContext) {
		RenderExtended render = choseRender(renderingContext);
		return render.render(renderingContext);
	}

	protected RenderExtended choseRender(RenderingContext renderingContext) {
		return (RenderExtended) renderingContext.getRenderFactory().getParameterExpressionImplLiteralRender(this, "default");
	}
	@Override
	public Type getType() {
		return expectedType;
	}

	@Override
	public boolean isJpaPositionalParameter() {
		return true;
	}
}
