
package com.finebi.analysis.api.criteria;

import java.util.List;
import java.util.Set;

/**
 * The <code>CriteriaQuery</code> interface defines functionality that is specific 
 * to top-level queries.
 *
 * @param <T>  the type of the defined result
 *
 * @since Java Persistence 2.0
 */
public interface CriteriaQuery<T> extends AbstractQuery<T> {
	
    /**
     * Specify the item that is to be returned in the query result.
     * Replaces the previously specified selection(s), if any.
     * 
     * <p> Note: Applications using the string-based API may need to
     * specify the type of the select item when it results from
     * a get or join operation and the query result type is 
     * specified. 
     *
     * <pre>
     *     For example:
     *
     *     CriteriaQuery&#060;String&#062; q = cb.createQuery(String.class);
     *     Root&#060;Order&#062; order = q.from(Order.class);
     *     q.select(order.get("shippingAddress").&#060;String&#062;get("state"));
     * 
     *     CriteriaQuery&#060;Product&#062; q2 = cb.createQuery(Product.class);
     *     q2.select(q2.from(Order.class)
     *                 .join("items")
     *                 .&#060;Item,Product&#062;join("product"));
     * 
     * </pre>
     * @param selection  selection specifying the item that
     *        is to be returned in the query result
     * @return the modified query
     * @throws IllegalArgumentException if the selection is
     *         a compound selection and more than one selection 
     *         item has the same assigned alias
     */
    CriteriaQuery<T> select(Selection<? extends T> selection);


    /**
     * Modify the query to restrict the query result according
     * to the specified boolean expression.
     * Replaces the previously added restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified query
     */
    CriteriaQuery<T> where(Expression<Boolean> restriction);

    /**
     * Modify the query to restrict the query result according 
     * to the conjunction of the specified restriction predicates.
     * Replaces the previously added restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param restrictions  zero or more restriction predicates
     * @return the modified query
     */
    CriteriaQuery<T> where(Predicate... restrictions);

    /**
     * Specify the expressions that are used to form groups over
     * the query results.
     * Replaces the previous specified grouping expressions, if any.
     * If no grouping expressions are specified, any previously 
     * added grouping expressions are simply removed.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param grouping  zero or more grouping expressions
     * @return the modified query
     */
    CriteriaQuery<T> groupBy(Expression<?>... grouping);

    /**
     * Specify the expressions that are used to form groups over
     * the query results.
     * Replaces the previous specified grouping expressions, if any.
     * If no grouping expressions are specified, any previously 
     * added grouping expressions are simply removed.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param grouping  list of zero or more grouping expressions
     * @return the modified query
     */
    CriteriaQuery<T> groupBy(List<Expression<?>> grouping);

    /**
     * Specify a restriction over the groups of the query.
     * Replaces the previous having restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified query
     */
    CriteriaQuery<T> having(Expression<Boolean> restriction);

    /**
     * Specify restrictions over the groups of the query
     * according the conjunction of the specified restriction 
     * predicates.
     * Replaces the previously added having restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param restrictions  zero or more restriction predicates
     * @return the modified query
     */
    CriteriaQuery<T> having(Predicate... restrictions);

    /**
     * Specify the ordering expressions that are used to
     * order the query results.
     * Replaces the previous ordering expressions, if any.
     * If no ordering expressions are specified, the previous
     * ordering, if any, is simply removed, and results will
     * be returned in no particular order.
     * The left-to-right sequence of the ordering expressions
     * determines the precedence, whereby the leftmost has highest
     * precedence.
     * @param o  zero or more ordering expressions
     * @return the modified query
     */
    CriteriaQuery<T> orderBy(Order... o);

    /**
     * Specify the ordering expressions that are used to
     * order the query results.
     * Replaces the previous ordering expressions, if any.
     * If no ordering expressions are specified, the previous
     * ordering, if any, is simply removed, and results will
     * be returned in no particular order.
     * The order of the ordering expressions in the list
     * determines the precedence, whereby the first element in the
     * list has highest precedence.
     * @param o  list of zero or more ordering expressions
     * @return the modified query
     */
    CriteriaQuery<T> orderBy(List<Order> o);

    /**
     * Specify whether duplicate query results will be eliminated.
     * A true value will cause duplicates to be eliminated.
     * A false value will cause duplicates to be retained.
     * If distinct has not been specified, duplicate results must
     * be retained.
     * This method only overrides the return type of the 
     * corresponding <code>AbstractQuery</code> method.
     * @param distinct  boolean value specifying whether duplicate
     *        results must be eliminated from the query result or
     *        whether they must be retained
     * @return the modified query.
     */
    CriteriaQuery<T> distinct(boolean distinct);
    
    /**
     * Return the ordering expressions in order of precedence.
     * Returns empty list if no ordering expressions have been
     * specified.
     * Modifications to the list do not affect the query.
     * @return the list of ordering expressions
     */
    List<Order> getOrderList();
 
    /**
     * Return the parameters of the query.  Returns empty set if
     * there are no parameters.
     * Modifications to the set do not affect the query.
     * @return the query parameters
     */
    Set<ParameterExpression<?>> getParameters();
}
