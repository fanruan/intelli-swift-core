
package com.finebi.datasource.sql.criteria.internal;
import com.finebi.datasource.api.criteria.ParameterExpression;

/**
 * A registry for parameters.  In criteria queries, parameters must be actively seeked out as expressions and predicates
 * are added to the {@link org.hibernate.criterion.CriteriaQuery}; this contract allows the various subcomponents to
 * register any parameters they contain.
 *
 * @author Steve Ebersole
 */
public interface ParameterRegistry {
	/**
	 * Registers the given parameter with this regitry.
	 *
	 * @param parameter The parameter to register.
	 */
	public void registerParameter(ParameterExpression<?> parameter);
}
