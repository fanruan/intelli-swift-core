
package com.finebi.datasource.sql.criteria.internal.predicate;

import java.io.Serializable;
import java.util.Collection;
import com.finebi.datasource.api.criteria.Expression;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.path.PluralAttributePath;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.LiteralExpression;

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
