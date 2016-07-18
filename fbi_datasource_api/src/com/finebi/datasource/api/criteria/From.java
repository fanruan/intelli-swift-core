
package com.finebi.datasource.api.criteria;

import com.finebi.datasource.api.metamodel.EntityType;

import java.util.Set;

/**
 * Represents a bound type, usually an entity that appears in
 * the from clause, but may also be an embeddable belonging to
 * an entity in the from clause.
 * <p> Serves as a factory for Joins of associations, embeddables, and
 * collections belonging to the type, and for Paths of attributes
 * belonging to the type.
 *
 * @param <Z> the source type
 * @param <X> the target type
 * @since Advanced FineBI Analysis 1.0
 */
@SuppressWarnings("hiding")
public interface From<Z, X> extends Path<X> {

    /**
     * Return the joins that have been made from this bound type.
     * Returns empty set if no joins have been made from this
     * bound type.
     * Modifications to the set do not affect the query.
     *
     * @return joins made from this type
     */
    Set<Join<X, ?>> getJoins();

    /**
     * Whether the <code>From</code> object has been obtained as a result of
     * correlation (use of a <code>Subquery</code> <code>correlate</code>
     * method).
     *
     * @return boolean indicating whether the object has been
     * obtained through correlation
     */
    boolean isCorrelated();

    /**
     * Returns the parent <code>From</code> object from which the correlated
     * <code>From</code> object has been obtained through correlation (use
     * of a <code>Subquery</code> <code>correlate</code> method).
     *
     * @return the parent of the correlated From object
     * @throws IllegalStateException if the From object has
     *                               not been obtained through correlation
     */
    From<Z, X> getCorrelationParent();



    Join join(EntityType entityType);



    <Y> Join<X, Y> join(EntityType<Y> entityType, JoinType jt);
    //String-based:


    EntityType<X> getEntityType();
}
