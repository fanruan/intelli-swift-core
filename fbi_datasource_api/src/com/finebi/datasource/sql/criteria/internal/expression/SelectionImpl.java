/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.api.criteria.Selection;
import com.finebi.datasource.sql.criteria.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterContainer;
import com.finebi.datasource.sql.criteria.internal.SelectionImplementor;
import com.finebi.datasource.sql.criteria.internal.ValueHandlerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * The Hibernate implementation of the JPA {@link Selection}
 * contract.
 *
 * @author Steve Ebersole
 */
public abstract class SelectionImpl<X>
		extends AbstractTupleElement<X>
		implements SelectionImplementor<X>, ParameterContainer, Serializable {
	public SelectionImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType) {
		super( criteriaBuilder, javaType );
	}

	public Selection<X> alias(String alias) {
		setAlias( alias );
		return this;
	}

	public boolean isCompoundSelection() {
		return false;
	}

	public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
		return getValueHandler() == null
				? null
				: Collections.singletonList( (ValueHandlerFactory.ValueHandler) getValueHandler() );
	}

	public List<Selection<?>> getCompoundSelectionItems() {
		throw new IllegalStateException( "Not a compound selection" );
	}
}
