
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
