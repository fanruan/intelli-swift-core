
package com.finebi.datasource.api.criteria;


import com.finebi.datasource.api.metamodel.CollectionAttribute;

import java.util.Collection;

/**
 * The <code>CollectionJoin</code> interface is the type of the result of
 * joining to a collection over an association or element 
 * collection that has been specified as a <code>java.util.Collection</code>.
 *
 * @param <Z> the source type of the join
 * @param <E> the element type of the target <code>Collection</code> 
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface CollectionJoin<Z, E> 
		extends PluralJoin<Z, Collection<E>, E> {

    /**
     *  Modify the join to restrict the result according to the
     *  specified ON condition and return the join object.  
     *  Replaces the previous ON condition, if any.
     *  @param restriction  a simple or compound boolean expression
     *  @return the modified join object
     *  @since Advanced FineBI Analysis 1.0
     */
    CollectionJoin<Z, E> on(Expression<Boolean> restriction);

    /**
     *  Modify the join to restrict the result according to the
     *  specified ON condition and return the join object.  
     *  Replaces the previous ON condition, if any.
     *  @param restrictions  zero or more restriction predicates
     *  @return the modified join object
     *  @since Advanced Fine BI 5.1
     */
    CollectionJoin<Z, E> on(Predicate... restrictions);

    /**
     * Return the metamodel representation for the collection
     * attribute.
     * @return metamodel type representing the <code>Collection</code> that is
     *         the target of the join
     */
    CollectionAttribute<? super Z, E> getModel();
}
