
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;
import com.finebi.datasource.api.criteria.Expression;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Models the ANSI SQL <tt>SQRT</tt> function.
 *
 * @author Steve Ebersole
 */
public class SqrtFunction
		extends ParameterizedFunctionExpression<Double>
		implements Serializable {
	public static final String NAME = "sqrt";

	public SqrtFunction(CriteriaBuilderImpl criteriaBuilder, Expression<? extends Number> expression) {
		super( criteriaBuilder, Double.class, NAME, expression );
	}

	@Override
	protected boolean isStandardJpaFunction() {
		return true;
	}
}
