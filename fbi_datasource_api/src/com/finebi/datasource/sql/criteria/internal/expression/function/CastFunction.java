/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression.function;

import com.finebi.datasource.sql.criteria.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;

import java.io.Serializable;

/**
 * Models a <tt>CAST</tt> function.
 *
 * @param <T> The cast result type.
 * @param <Y> The type of the expression to be cast.
 *
 * @author Steve Ebersole
 */
public class CastFunction<T,Y>
		extends BasicFunctionExpression<T>
		implements FunctionExpression<T>, Serializable {
	public static final String CAST_NAME = "cast";

	private final ExpressionImpl<Y> castSource;

	public CastFunction(
			CriteriaBuilderImpl criteriaBuilder,
			Class<T> javaType,
			ExpressionImpl<Y> castSource) {
		super( criteriaBuilder, javaType, CAST_NAME );
		this.castSource = castSource;
	}

	public ExpressionImpl<Y> getCastSource() {
		return castSource;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		Helper.possibleParameter( getCastSource(), registry );
	}


}
