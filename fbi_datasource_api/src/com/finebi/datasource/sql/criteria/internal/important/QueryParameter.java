
package com.finebi.datasource.sql.criteria.internal.important;

import com.finebi.datasource.sql.criteria.internal.Type;

/**
 * NOTE: Consider this contract (and its sub-contracts) as incubating as we transition to 6.0 and SQM
 *
 * @author Steve Ebersole
 */
public interface QueryParameter<T> extends com.finebi.datasource.api.Parameter<T> {
	/**
	 * Get the Hibernate Type associated with this parameter.
	 *
	 * @return The Hibernate Type.
	 */
	Type getType();

	/**
	 * JPA has a different definition of positional parameters than what legacy Hibernate HQL had.  In JPA,
	 * the parameter holders are labelled (named :/).  At any rate the semantics are different and we often
	 * need to understand which we are dealing with (and applications might too).
	 *
	 * @return {@code true} if this is a JPA-style positional parameter; {@code false} would indicate
	 * we have either a named parameter ({@link #getName()} would return a non-{@code null} value) or a native
	 * Hibernate positional parameter.
	 */
	boolean isJpaPositionalParameter();

	// todo : add a method indicating whether this parameter is valid for use in "parameter list binding"
}
