
package com.finebi.datasource.api;

import com.finebi.datasource.api.metamodel.EntityType;

/**
 * The <code>TupleElement</code> interface defines an element that is returned in
 * a query result tuple.
 *
 * @param <X> the type of the element
 * @see Tuple
 * @since Advanced FineBI Analysis 1.0
 */
public interface TupleElement<X> {

    /**
     * Return the Java type of the tuple element.
     *
     * @return the Java type of the tuple element
     */


    Class<X> getJavaType();

    /**
     * Return the alias assigned to the tuple element or null,
     * if no alias has been assigned.
     *
     * @return alias
     */
    String getAlias();
}
