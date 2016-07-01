
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;
import java.sql.Timestamp;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Models the ANSI SQL <tt>CURRENT_TIMESTAMP</tt> function.
 *
 * @author Steve Ebersole
 */
public class CurrentTimestampFunction
		extends BasicFunctionExpression<Timestamp>
		implements Serializable {
	public static final String NAME = "current_timestamp";

	public CurrentTimestampFunction(CriteriaBuilderImpl criteriaBuilder) {
		super( criteriaBuilder, Timestamp.class, NAME );
	}
}
