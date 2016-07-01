
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;
import com.finebi.datasource.api.metamodel.ListAttribute;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.PathImplementor;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;


/**
 * An expression for referring to the index of a list.
 *
 * @author Steve Ebersole
 */
public class ListIndexExpression extends ExpressionImpl<Integer> implements Serializable {
	private final PathImplementor origin;
	private final ListAttribute listAttribute;

	public ListIndexExpression(
			CriteriaBuilderImpl criteriaBuilder,
			PathImplementor origin,
			ListAttribute listAttribute) {
		super( criteriaBuilder, Integer.class );
		this.origin = origin;
		this.listAttribute = listAttribute;
	}

	public ListAttribute getListAttribute() {
		return listAttribute;
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do
	}

	public String render(RenderingContext renderingContext) {
		return "index("
				+ origin.getPathIdentifier()
				+ ")";
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
}
