/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.sql.criteria.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.*;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import com.finebi.datasource.api.Tuple;
import com.finebi.datasource.api.criteria.CompoundSelection;
import com.finebi.datasource.api.criteria.Selection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Hibernate implementation of the JPA {@link CompoundSelection}
 * contract.
 *
 * @author Steve Ebersole
 */
public class CompoundSelectionImpl<X>
		extends SelectionImpl<X>
		implements CompoundSelection<X>, Renderable, Serializable {
	private final boolean isConstructor;
	private List<Selection<?>> selectionItems;

	public CompoundSelectionImpl(
			CriteriaBuilderImpl criteriaBuilder,
			Class<X> javaType,
			List<Selection<?>> selectionItems) {
		super( criteriaBuilder, javaType );
		this.isConstructor = !javaType.isArray() && !Tuple.class.isAssignableFrom( javaType );
		this.selectionItems = selectionItems;
	}

	@Override
	public boolean isCompoundSelection() {
		return true;
	}

	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return selectionItems;
	}

	@Override
	public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
		if ( isConstructor ) {
			return null;
		}
		boolean foundHandlers = false;
		ArrayList<ValueHandlerFactory.ValueHandler> valueHandlers = new ArrayList<ValueHandlerFactory.ValueHandler>();
		for ( Selection selection : getCompoundSelectionItems() ) {
			ValueHandlerFactory.ValueHandler valueHandler = ( (TupleElementImplementor) selection ).getValueHandler();
			valueHandlers.add( valueHandler );
			foundHandlers = foundHandlers || valueHandler != null;
		}
		return foundHandlers ? null : valueHandlers;
	}

	public void registerParameters(ParameterRegistry registry) {
		for ( Selection selectionItem : getCompoundSelectionItems() ) {
			Helper.possibleParameter(selectionItem, registry);
		}
	}

	public String render(RenderingContext renderingContext) {
		StringBuilder buff = new StringBuilder();
		if ( isConstructor ) {
			buff.append( "new " ).append( getJavaType().getName() ).append( '(' );
		}
		String sep = "";
		for ( Selection selection : selectionItems ) {
			buff.append( sep )
					.append( ( (Renderable) selection ).renderProjection( renderingContext ) );
			sep = ", ";
		}
		if ( isConstructor ) {
			buff.append( ')' );
		}
		return buff.toString();
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
}
