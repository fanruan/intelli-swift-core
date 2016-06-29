/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.LiteralExpression;
import com.finebi.datasource.sql.criteria.internal.path.PluralAttributePath;

import com.finebi.datasource.api.criteria.Expression;
import java.io.Serializable;
import java.util.Collection;

/**
 * Models an <tt>[NOT] MEMBER OF</tt> restriction
 *
 * @author Steve Ebersole
 */
public class MemberOfPredicate<E, C extends Collection<E>>
		extends AbstractSimplePredicate
		implements Serializable {

	private final Expression<E> elementExpression;
	private final PluralAttributePath<C> collectionPath;

	public MemberOfPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<E> elementExpression,
			PluralAttributePath<C> collectionPath) {
		super( criteriaBuilder );
		this.elementExpression = elementExpression;
		this.collectionPath = collectionPath;
	}

	public MemberOfPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			E element,
			PluralAttributePath<C> collectionPath) {
		this(
				criteriaBuilder,
				new LiteralExpression<E>( criteriaBuilder, element ),
				collectionPath
		);
	}

	public PluralAttributePath<C> getCollectionPath() {
		return collectionPath;
	}

	public Expression<E> getElementExpression() {
		return elementExpression;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		Helper.possibleParameter( getCollectionPath(), registry );
		Helper.possibleParameter( getElementExpression(), registry );
	}

	@Override
	public String render(boolean isNegated, RenderingContext renderingContext) {
		return ( (Renderable) elementExpression ).render( renderingContext )
				+ ( isNegated ? " not" : "" ) + " member of "
				+ getCollectionPath().render( renderingContext );
	}
}
