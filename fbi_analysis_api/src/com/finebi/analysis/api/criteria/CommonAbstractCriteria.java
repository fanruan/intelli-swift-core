
package com.finebi.analysis.api.criteria;

/**
 * The <code>CommonAbstractCriteria</code> interface defines functionality 
 * that is common to both top-level criteria queries and subqueries as 
 * well as to update and delete criteria operations.
 * It is not intended to be used directly in query construction.
 *
 * <p> Note that criteria queries and criteria update and delete operations
 * are typed differently.
 * Criteria queries are typed according to the query result type.
 * Update and delete operations are typed according to the target of the
 * update or delete.
 *
 * @since Java Persistence 2.1
 */
public interface CommonAbstractCriteria {

    /**
     * Create a subquery of the query. 
     * @param type  the subquery result type
     * @return subquery 
     */
    <U> Subquery<U> subquery(Class<U> type);

    /**
     * Return the predicate that corresponds to the where clause
     * restriction(s), or null if no restrictions have been
     * specified.
     * @return where clause predicate
     */
    Predicate getRestriction();
 
}
