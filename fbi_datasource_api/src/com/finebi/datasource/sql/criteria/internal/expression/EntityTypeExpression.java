
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class EntityTypeExpression<T> extends ExpressionImpl<T> implements Serializable {
	public EntityTypeExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType) {
		super( criteriaBuilder, javaType );
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothign to do
	}

	public String render(RenderingContext renderingContext) {
		// todo : is it valid for this to get rendered into the query itself?
		throw new IllegalArgumentException( "Unexpected call on EntityTypeExpression#render" );
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
}
