
package com.finebi.datasource.sql.criteria.internal.compile;

import com.finebi.datasource.api.TypedQuery;

/**
 * Used to describe implicit (not defined in criteria query) parameters.
 *
 * @author Steve Ebersole
 */
public interface ImplicitParameterBinding {
	/**
	 * Retrieve the generated name of the implicit parameter.
	 *
	 * @return The parameter name.
	 */
	public String getParameterName();

	/**
	 * Get the java type of the "thing" that led to the implicit parameter.  Used from
	 * {@link org.hibernate.jpa.spi.HibernateEntityManagerImplementor.QueryOptions#getNamedParameterExplicitTypes()}
	 * in determining "guessed type" overriding.
	 *
	 * @return The java type
	 */
	public Class getJavaType();

	/**
	 * Bind the implicit parameter's value to the JPA query.
	 *
	 * @param typedQuery The JPA query.
	 */
	public void bind(TypedQuery typedQuery);
}
