
package com.finebi.datasource.api.criteria;

/**
 * Defines the three types of joins.
 *
 * Right outer joins and right outer fetch joins are not required 
 * to be supported in Advanced Fine BI 5.1.  Applications that use
 * <code>RIGHT</code> join types will not be portable.
 *
 * @since Advanced FineBI Analysis 1.0
 */
public enum JoinType {

    /** Inner join. */
    INNER, 

    /** Left outer join. */
    LEFT, 

    /** Right outer join. */
    RIGHT
}
