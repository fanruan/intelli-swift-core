package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.TupleElement;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
public interface TupleElementImplementor<X> extends TupleElement<X> {
    public ValueHandlerFactory.ValueHandler<X> getValueHandler();
}
