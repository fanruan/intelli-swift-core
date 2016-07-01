
package com.finebi.datasource.sql.criteria.internal.predicate;

import java.io.Serializable;
import java.util.List;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.api.criteria.Selection;

import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;

/**
 * Basic template support for {@link Predicate} implementors providing
 * expression handling, negation and conjunction/disjunction handling.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractPredicateImpl
		extends ExpressionImpl<Boolean>
		implements PredicateImplementor, Serializable {

	protected AbstractPredicateImpl(CriteriaBuilderImpl criteriaBuilder) {
		super( criteriaBuilder, Boolean.class );
	}

	public boolean isNegated() {
		return false;
	}

	public Predicate not() {
		return new NegatedPredicateWrapper( this );
	}


	// Selection ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Override
	public final boolean isCompoundSelection() {
		// Should always be false for predicates
		return super.isCompoundSelection();
	}

	@Override
	public final List<Selection<?>> getCompoundSelectionItems() {
		// Should never have sub selection items for predicates
		return super.getCompoundSelectionItems();
	}

}
