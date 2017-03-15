
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;
import com.finebi.datasource.api.criteria.Expression;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Models the ANSI SQL <tt>UPPER</tt> function.
 *
 * @author Steve Ebersole
 */
public class UpperFunction
		extends ParameterizedFunctionExpression<String>
		implements Serializable {
	public static final String NAME = "upper";

	public UpperFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> string) {
		super( criteriaBuilder, String.class, NAME, string );
	}

	@Override
	protected boolean isStandardJpaFunction() {
		return true;
	}
}
