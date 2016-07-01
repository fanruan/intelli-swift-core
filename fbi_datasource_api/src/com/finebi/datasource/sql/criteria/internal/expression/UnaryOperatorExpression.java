
package com.finebi.datasource.sql.criteria.internal.expression;
import java.io.Serializable;
import com.finebi.datasource.api.criteria.Expression;

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
