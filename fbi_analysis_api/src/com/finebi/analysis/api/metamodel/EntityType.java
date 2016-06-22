
package com.finebi.analysis.api.metamodel;

/**
 *  Instances of the type <code>EntityType</code> represent entity types.
 *
 *  @param <X> The represented entity type.
 *
<<<<<<< HEAD
 * @since Advanced FineBI Analysis 1.0
=======
 * @since Java Persistence 2.0
>>>>>>> JPA接口
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
