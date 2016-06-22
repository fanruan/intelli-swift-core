
package com.finebi.analysis.api.metamodel;

/**
 *  Instances of the type <code>EntityType</code> represent entity types.
 *
 *  @param <X> The represented entity type.
 *
 * @since Java Persistence 2.0
 *
 */
public interface EntityType<X> 
            extends IdentifiableType<X>, Bindable<X>{

    /**
     *  Return the entity name.
     *  @return entity name
     */
    String getName();
}
