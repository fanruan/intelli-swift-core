
package com.finebi.datasource.sql.criteria.internal;
import com.finebi.datasource.api.TupleElement;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public interface TupleElementImplementor<X> extends TupleElement<X> {
	public ValueHandlerFactory.ValueHandler<X> getValueHandler();
}
