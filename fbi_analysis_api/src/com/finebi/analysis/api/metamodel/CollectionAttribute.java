
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>CollectionAttribute</code> represent persistent 
 * <code>java.util.Collection</code>-valued attributes.
 *
 * @param <X> The type the represented Collection belongs to
 * @param <E> The element type of the represented Collection
 *
 * @since Java Persistence 2.0
 *
 */
public interface CollectionAttribute<X, E> 
	extends PluralAttribute<X, java.util.Collection<E>, E> {}
