/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import com.finebi.datasource.api.criteria.From;

/**
 * Implementation contract for the JPA {@link From} interface.
 *
 * @author Steve Ebersole
 */
public interface FromImplementor<Z,X> extends PathImplementor<X>, From<Z,X> {
	public void prepareAlias(RenderingContext renderingContext);



	public FromImplementor<Z,X> correlateTo(CriteriaSubqueryImpl subquery);
	public void prepareCorrelationDelegate(FromImplementor<Z, X> parent);
	public FromImplementor<Z, X> getCorrelationParent();
}
