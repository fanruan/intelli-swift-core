
package com.finebi.datasource.api.criteria;


import com.finebi.datasource.api.TupleElement;

import java.util.List;

/**
 * The <code>Selection</code> interface defines an item that is to be
 * returned in a query result.
 *
 * @param <X> the type of the selection item
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface Selection<X> extends TupleElement<X> {

    /**
     * Assigns an alias to the selection item.
     * Once assigned, an alias cannot be changed or reassigned.
     * Returns the same selection item.
     * @param name  alias
     * @return selection item 
     */
    Selection<X> alias(String name);

    /**
     * Whether the selection item is a compound selection.
     * @return boolean indicating whether the selection is a compound
     *         selection
     */
    boolean isCompoundSelection();

    /**
     * Return the selection items composing a compound selection.
     * Modifications to the list do not affect the query.
     * @return list of selection items
     * @throws IllegalStateException if selection is not a 
     *         compound selection
     */
    List<Selection<?>> getCompoundSelectionItems();
}
