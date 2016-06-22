
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>SetAttribute</code> represent
 * persistent <code>java.util.Set</code>-valued attributes.
 *
 * @param <X> The type the represented Set belongs to
 * @param <E> The element type of the represented Set
 *
<<<<<<< HEAD
 * @since Advanced FineBI Analysis 1.0
=======
 * @since Java Persistence 2.0
>>>>>>> JPA接口
 */
public interface SetAttribute<X, E> 
	extends PluralAttribute<X, java.util.Set<E>, E> {} 
