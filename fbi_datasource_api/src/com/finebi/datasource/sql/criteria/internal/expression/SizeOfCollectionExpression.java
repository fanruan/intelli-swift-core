
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.PluralAttributePath;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a "size of" expression in regards to a persistent collection; the implication is
 * that of a subquery.
 *
 * @author Steve Ebersole
 */
public class SizeOfCollectionExpression<C extends Collection>
		extends ExpressionImpl<Integer>
		implements Serializable {
	private final PluralAttributePath<C> collectionPath;

	public SizeOfCollectionExpression(
			CriteriaBuilderImpl criteriaBuilder,
			PluralAttributePath<C> collectionPath) {
		super( criteriaBuilder, Integer.class);
		this.collectionPath = collectionPath;
	}

	public PluralAttributePath<C> getCollectionPath() {
		return collectionPath;
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do
	}

	public String render(RenderingContext renderingContext) {
		return "size(" + getCollectionPath().render( renderingContext ) + ")";
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
}
