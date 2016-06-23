
package com.finebi.analysis.api.criteria;

import com.finebi.analysis.api.metamodel.*;

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

    /**
     * Create an inner join to the specified single-valued
     * attribute.
     *
     * @param attribute target of the join
     * @return the resulting join
     */
    <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute);

    Join join(PlainTable attribute);

    /**
     * Create a join to the specified single-valued attribute
     * using the given join type.
     *
     * @param attribute target of the join
     * @param jt        join type
     * @return the resulting join
     */
    <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt);



    //String-based:

    /**
     * Create an inner join to the specified attribute.
     *
     * @param attributeName name of the attribute for the
     *                      target of the join
     * @return the resulting join
     * @throws IllegalArgumentException if attribute of the given
     *                                  name does not exist
     */
    <X, Y> Join<X, Y> join(String attributeName);




    /**
     * Create a join to the specified attribute using the given
     * join type.
     *
     * @param attributeName name of the attribute for the
     *                      target of the join
     * @param jt            join type
     * @return the resulting join
     * @throws IllegalArgumentException if attribute of the given
     *                                  name does not exist
     */
    <X, Y> Join<X, Y> join(String attributeName, JoinType jt);



}
