
package com.finebi.datasource.sql.criteria.internal.predicate;

import java.io.Serializable;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * Predicate used to assert a static boolean condition.
 *
 * @author Steve Ebersole
 */
public class BooleanStaticAssertionPredicate
		extends AbstractSimplePredicate
		implements Serializable {
	private final Boolean assertedValue;

	public BooleanStaticAssertionPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Boolean assertedValue) {
		super( criteriaBuilder );
		this.assertedValue = assertedValue;
	}

	public Boolean getAssertedValue() {
		return assertedValue;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		// nada
	}

	@Override
	public String render(boolean isNegated, RenderingContext renderingContext) {
		boolean isTrue = getAssertedValue();
		if ( isNegated ) {
			isTrue = !isTrue;
		}
		return isTrue ? "1=1" : "0=1";
	}

}
