
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;
import java.sql.Time;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Models the ANSI SQL <tt>CURRENT_TIME</tt> function.
 *
 * @author Steve Ebersole
 */
public class CurrentTimeFunction
		extends BasicFunctionExpression<Time> 
		implements Serializable {
	public static final String NAME = "current_time";

	public CurrentTimeFunction(CriteriaBuilderImpl criteriaBuilder) {
		super( criteriaBuilder, Time.class, NAME );
	}
}
