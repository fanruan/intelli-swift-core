
package com.finebi.datasource.sql.criteria.internal;
import com.finebi.datasource.api.criteria.From;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * Implementation contract for the JPA {@link From} interface.
 *
 * @author Steve Ebersole
 */
public interface FromImplementor<Z,X> extends PathImplementor<X>, From<Z,X> {
	public void prepareAlias(RenderingContext renderingContext);
	public String renderTableExpression(RenderingContext renderingContext);


	public FromImplementor<Z,X> correlateTo(CriteriaSubqueryImpl subquery);
	public void prepareCorrelationDelegate(FromImplementor<Z,X> parent);
	public FromImplementor<Z, X> getCorrelationParent();
}
