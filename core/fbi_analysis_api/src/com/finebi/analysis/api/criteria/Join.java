
package com.finebi.analysis.api.criteria;

/**
 * A join to an entity, embeddable, or basic type.
 *
 * @param <Z> the source type of the join
 * @param <X> the target type of the join
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface Join<Z, X> extends From<Z, X> {

    /**
     *  Modify the join to restrict the result according to the
     *  specified ON condition and return the join object.  
     *  Replaces the previous ON condition, if any.
     *  @param restriction  a simple or compound boolean expression
     *  @return the modified join object
     *  @since Advanced Fine BI 5.1
     */
    Join<Z, X> on(Expression<Boolean> restriction);

    /**
     *  Modify the join to restrict the result according to the
     *  specified ON condition and return the join object.  
     *  Replaces the previous ON condition, if any.
     *  @param restrictions  zero or more restriction predicates
     *  @return the modified join object
     *  @since Advanced Fine BI 5.1
     */
    Join<Z, X> on(Predicate... restrictions);

    /** 
     *  Return the predicate that corresponds to the ON 
     *  restriction(s) on the join, or null if no ON condition 
     *  has been specified.
     *  @return the ON restriction predicate
     *  @since Advanced Fine BI 5.1
     */
    Predicate getOn();


    /**
     * Return the parent of the join.
     * @return join parent
     */
    From<?, Z> getParent();

    /**
     * Return the join type.
     * @return join type
     */
    JoinType getJoinType();
}
