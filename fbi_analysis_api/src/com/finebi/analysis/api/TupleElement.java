
package com.finebi.analysis.api;

/**
 * The <code>TupleElement</code> interface defines an element that is returned in
 * a query result tuple.
 * @param <X> the type of the element
 *
 * @see Tuple
 *
<<<<<<< HEAD
 * @since Advanced FineBI Analysis 1.0
=======
 * @since Java Persistence 2.0
>>>>>>> JPA接口
 */
public interface TupleElement<X> {
    
    /**
     * Return the Java type of the tuple element.
     * @return the Java type of the tuple element
     */
    Class<? extends X> getJavaType();

    /**
     * Return the alias assigned to the tuple element or null, 
     * if no alias has been assigned.
     * @return alias
     */
    String getAlias();
}
