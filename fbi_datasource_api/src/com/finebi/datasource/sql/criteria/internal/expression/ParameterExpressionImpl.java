/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.sql.QueryParameter;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.ExplicitParameterInfo;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import org.hibernate.type.Type;

import com.finebi.datasource.api.criteria.ParameterExpression;
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
		final ExplicitParameterInfo parameterInfo = renderingContext.registerExplicitParameter( this );
		return parameterInfo.render();
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
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
