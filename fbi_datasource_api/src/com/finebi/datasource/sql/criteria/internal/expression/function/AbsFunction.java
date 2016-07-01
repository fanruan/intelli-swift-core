
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;
import com.finebi.datasource.api.criteria.Expression;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Models the ANSI SQL <tt>ABS</tt> function.
 *
 * @author Steve Ebersole
 */
public class AbsFunction<N extends Number>
		extends ParameterizedFunctionExpression<N>
		implements Serializable {
	public static final String NAME = "abs";

	public AbsFunction(CriteriaBuilderImpl criteriaBuilder, Expression expression) {
		super( criteriaBuilder, expression.getJavaType(), NAME, expression );
	}

	@Override
	protected boolean isStandardJpaFunction() {
		return true;
	}
}
