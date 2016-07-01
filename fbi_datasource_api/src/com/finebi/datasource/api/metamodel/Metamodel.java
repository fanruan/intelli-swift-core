
package com.finebi.datasource.api.metamodel;

import java.util.Set;

/**
 * Provides access to the metamodel of persistent
 * entities in the persistence unit. 
 *
 * @since Advanced FineBI Analysis 1.0
 * @author Connery
 */
public interface Metamodel {

    /**
     *  Return the metamodel entity type representing the entity.
     *  @param cls  the type of the represented entity
     *  @return the metamodel entity type
     *  @throws IllegalArgumentException if not an entity
     */
    <X> EntityType<X> entity(Class<X> cls);

    /**
     *  Return the metamodel managed type representing the 
     *  entity, mapped superclass, or embeddable class.
     *  @param cls  the type of the represented managed class
     *  @return the metamodel managed type
     *  @throws IllegalArgumentException if not a managed class
     */
    <X> ManagedType<X> managedType(Class<X> cls);
    
    /**
     *  Return the metamodel embeddable type representing the
     *  embeddable class.
     *  @param cls  the type of the represented embeddable class
     *  @return the metamodel embeddable type
     *  @throws IllegalArgumentException if not an embeddable class
     */
    <X> EmbeddableType<X> embeddable(Class<X> cls);

    /**
     *  Return the metamodel managed types.
     *  @return the metamodel managed types
     */
    Set<ManagedType<?>> getManagedTypes();

    /**
     * Return the metamodel entity types.
     * @return the metamodel entity types
     */
    Set<EntityType<?>> getEntities();

    /**
     * Return the metamodel embeddable types.  Returns empty set
     * if there are no embeddable types.
     * @return the metamodel embeddable types
     */
    Set<EmbeddableType<?>> getEmbeddables();
}
