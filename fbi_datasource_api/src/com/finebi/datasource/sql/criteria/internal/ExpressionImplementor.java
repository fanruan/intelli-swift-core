
package com.finebi.datasource.sql.criteria.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import com.finebi.datasource.api.criteria.Expression;

/**
 * Internal contract for implementations of the JPA {@link Expression} contract.
 *
 * @author Steve Ebersole
 */
public interface ExpressionImplementor<T> extends SelectionImplementor<T>, Expression<T>, Renderable {
	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toLong}
	 *
	 * @return <tt>this</tt> but as a long
	 */
	public ExpressionImplementor<Long> asLong();

	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toInteger}
	 *
	 * @return <tt>this</tt> but as an integer
	 */
	public ExpressionImplementor<Integer> asInteger();

	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toFloat}
	 *
	 * @return <tt>this</tt> but as a float
	 */
	public ExpressionImplementor<Float> asFloat();

	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toDouble}
	 *
	 * @return <tt>this</tt> but as a double
	 */
	public ExpressionImplementor<Double> asDouble();

	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toBigDecimal}
	 *
	 * @return <tt>this</tt> but as a {@link BigDecimal}
	 */
	public ExpressionImplementor<BigDecimal> asBigDecimal();

	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toBigInteger}
	 *
	 * @return <tt>this</tt> but as a {@link BigInteger}
	 */
	public ExpressionImplementor<BigInteger> asBigInteger();

	/**
	 * See {@link com.finebi.datasource.api.criteria.CriteriaBuilder#toString}
	 *
	 * @return <tt>this</tt> but as a string
	 */
	public ExpressionImplementor<String> asString();
}
