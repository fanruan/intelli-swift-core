
package com.finebi.datasource.api.criteria;

import java.util.List;

/**
 * The type of a simple or compound predicate: a conjunction or
 * disjunction of restrictions.
 * A simple predicate is considered to be a conjunction with a
 * single conjunct.
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface Predicate extends Expression<Boolean> {

        public static enum BooleanOperator {
              AND, OR
        }
	
    /**
     * Return the boolean operator for the predicate.
     * If the predicate is simple, this is <code>AND</code>.
     * @return boolean operator for the predicate
     */
    BooleanOperator getOperator();
    
    /**
     * Whether the predicate has been created from another
     * predicate by applying the <code>Predicate.not()</code> method
     * or the <code>CriteriaBuilder.not()</code> method.
     * @return boolean indicating if the predicate is 
     *                 a negated predicate
     */
    boolean isNegated();

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     * Returns empty list if there are no top-level conjuncts or
     * disjuncts of the predicate.
     * Modifications to the list do not affect the query.
     * @return list of boolean expressions forming the predicate
     */
    List<Expression<Boolean>> getExpressions();

    /**
     * Create a negation of the predicate.
     * @return negated predicate 
     */
    Predicate not();

}
