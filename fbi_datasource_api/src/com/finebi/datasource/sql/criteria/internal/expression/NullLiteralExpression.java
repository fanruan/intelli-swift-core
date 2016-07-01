
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * Represents a <tt>NULL</tt>literal expression.
 *
 * @author Steve Ebersole
 */
public class NullLiteralExpression<T> extends ExpressionImpl<T> implements Serializable {
	public NullLiteralExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> type) {
		super( criteriaBuilder, type );
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do
	}

	public String render(RenderingContext renderingContext) {
		return "null";
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
}
