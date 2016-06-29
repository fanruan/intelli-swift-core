/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.UnaryOperatorExpression;
import com.finebi.datasource.sql.criteria.internal.path.PluralAttributePath;

import java.io.Serializable;
import java.util.Collection;

/**
 * Models an <tt>IS [NOT] EMPTY</tt> restriction
 *
 * @author Steve Ebersole
 */
public class IsEmptyPredicate<C extends Collection>
		extends AbstractSimplePredicate
		implements UnaryOperatorExpression<Boolean>, Serializable {

	private final PluralAttributePath<C> collectionPath;

	public IsEmptyPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			PluralAttributePath<C> collectionPath) {
		super( criteriaBuilder );
		this.collectionPath = collectionPath;
	}

	@Override
	public PluralAttributePath<C> getOperand() {
		return collectionPath;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		// nothing to do
	}

	@Override
	public String render(boolean isNegated, RenderingContext renderingContext) {
		final String operator = isNegated ? " is not empty" : " is empty";
		return getOperand().render( renderingContext ) + operator;
	}
}
