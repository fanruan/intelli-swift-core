/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal;
import java.util.List;
import com.finebi.datasource.api.criteria.Selection;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public interface SelectionImplementor<X> extends TupleElementImplementor<X>, Selection<X>  {
	public List<ValueHandlerFactory.ValueHandler> getValueHandlers();
}
