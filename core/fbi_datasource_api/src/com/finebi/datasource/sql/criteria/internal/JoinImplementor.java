
package com.finebi.datasource.sql.criteria.internal;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Join;
import com.finebi.datasource.api.criteria.Predicate;

/**
 * Consolidates the {@link Join} and {@link Fetch} hierarchies since that is how we implement them.
 * This allows us to treat them polymorphically.
*
* @author Steve Ebersole
*/
public interface JoinImplementor<Z,X> extends Join<Z,X>, FromImplementor<Z,X> {
	/**
	 * Refined return type
	 */
	@Override
	public JoinImplementor<Z,X> correlateTo(CriteriaSubqueryImpl subquery);

	/**
	 * Coordinate return type between {@link Join#on(Expression)}
	 */
	@Override
	public JoinImplementor<Z, X> on(Expression<Boolean> restriction);

	/**
	 * Coordinate return type between {@link Join#on(Predicate...)}
	 */
	@Override
	public JoinImplementor<Z, X> on(Predicate... restrictions);


}
