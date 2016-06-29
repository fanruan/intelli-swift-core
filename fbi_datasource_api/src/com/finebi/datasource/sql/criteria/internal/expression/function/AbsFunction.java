/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression.function;

import com.finebi.datasource.api.criteria.Expression;
import java.io.Serializable;

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
