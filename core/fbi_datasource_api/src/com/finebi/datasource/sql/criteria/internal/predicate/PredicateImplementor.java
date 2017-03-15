
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Predicate;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * @author Steve Ebersole
 */
public interface PredicateImplementor extends Predicate, Renderable {
	/**
	 * Access to the CriteriaBuilder
	 *
	 * @return The CriteriaBuilder
	 */
	public CriteriaBuilderImpl criteriaBuilder();

	/**
	 * Is this a conjunction or disjunction?
	 *
	 * @return {@code true} if this predicate is a junction (AND/OR); {@code false} otherwise
	 */
	public boolean isJunction();

	/**
	 * Form of {@link Renderable#render} used when the predicate is wrapped in a negated wrapper.  Allows passing
	 * down the negation flag.
	 * <p/>
	 * Note that this form is no-op in compound (junction) predicates.  The reason being that compound predicates
	 * are more complex and the negation is applied during its creation.
	 *
	 * @param isNegated Should the predicate be negated.
	 * @param renderingContext The context for rendering
	 *
	 * @return The rendered predicate fragment.
	 */
	public Object render(boolean isNegated, RenderingContext renderingContext);
}






















