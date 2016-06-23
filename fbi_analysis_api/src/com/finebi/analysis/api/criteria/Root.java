
package com.finebi.analysis.api.criteria;

import com.finebi.analysis.api.metamodel.EntityType;

/**
 * A root type in the from clause.
 * Query roots always reference entities.
 *
 * @param <X> the entity type referenced by the root
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface Root<X> extends From<X, X> {

    /**
     * Return the metamodel entity corresponding to the root.
     * @return metamodel entity corresponding to the root
     */
    EntityType<X> getModel();
}
