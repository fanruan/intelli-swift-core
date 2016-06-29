/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.api.criteria.Expression;
import java.io.Serializable;

/**
 * Contract for operators with a single operand.
 *
 * @author Steve Ebersole
 */
public interface UnaryOperatorExpression<T> extends Expression<T>, Serializable {
	/**
	 * Get the operand.
	 *
	 * @return The operand.
	 */
	public Expression<?> getOperand();
}
