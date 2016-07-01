
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;
import com.finebi.datasource.api.criteria.Expression;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Models the ANSI SQL <tt>LOWER</tt> function.
 *
 * @author Steve Ebersole
 */
public class LowerFunction
		extends ParameterizedFunctionExpression<String>
		implements Serializable {
	public static final String NAME = "lower";

	public LowerFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> string) {
		super( criteriaBuilder, String.class, NAME, string );
	}

	@Override
	protected boolean isStandardJpaFunction() {
		return true;
	}
}
