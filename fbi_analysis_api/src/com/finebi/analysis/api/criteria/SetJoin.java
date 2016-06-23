
package com.finebi.analysis.api.criteria;

import com.finebi.analysis.api.metamodel.SetAttribute;
import java.util.Set;

/**
 * The <code>SetJoin</code> interface is the type of the result of
 * joining to a collection over an association or element 
 * collection that has been specified as a <code>java.util.Set</code>.
 *
 * @param <Z> the source type of the join
 * @param <E> the element type of the target <code>Set</code> 
 *
 * @since Java Persistence 2.0
 */
public interface SetJoin<Z, E> extends PluralJoin<Z, Set<E>, E> {

    /**
     *  Modify the join to restrict the result according to the
     *  specified ON condition and return the join object.  
     *  Replaces the previous ON condition, if any.
     *  @param restriction  a simple or compound boolean expression
     *  @return the modified join object
     *  @since Java Persistence 2.1
     */
    SetJoin<Z, E> on(Expression<Boolean> restriction);

    /**
     *  Modify the join to restrict the result according to the
     *  specified ON condition and return the join object.  
     *  Replaces the previous ON condition, if any.
     *  @param restrictions  zero or more restriction predicates
     *  @return the modified join object
     *  @since Java Persistence 2.1
     */
    SetJoin<Z, E> on(Predicate... restrictions);

    /**
     * Return the metamodel representation for the set attribute.
     * @return metamodel type representing the <code>Set</code> that is
     *         the target of the join
     */
    SetAttribute<? super Z, E> getModel();
}
