/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.sql.criteria.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.ValueHandlerFactory;

import java.io.Serializable;

/**
 * Represents a literal expression.
 *
 * @author Steve Ebersole
 */
public class LiteralExpression<T> extends ExpressionImpl<T> implements Serializable {
	private Object literal;

	@SuppressWarnings({ "unchecked" })
	public LiteralExpression(CriteriaBuilderImpl criteriaBuilder, T literal) {
		this( criteriaBuilder, (Class<T>) determineClass( literal ), literal );
	}

	private static Class determineClass(Object literal) {
		return literal == null ? null : literal.getClass();
	}

	public LiteralExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> type, T literal) {
		super( criteriaBuilder, type );
		this.literal = literal;
	}

	@SuppressWarnings({ "unchecked" })
	public T getLiteral() {
		return (T) literal;
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do
	}




	@Override
	@SuppressWarnings({ "unchecked" })
	protected void resetJavaType(Class targetType) {
		super.resetJavaType( targetType );
		ValueHandlerFactory.ValueHandler valueHandler = getValueHandler();
		if ( valueHandler == null ) {
			valueHandler = ValueHandlerFactory.determineAppropriateHandler( targetType );
			forceConversion( valueHandler );
		}

		if ( valueHandler != null ) {
			literal = valueHandler.convert( literal );
		}
	}
}
