
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>ListAttribute</code> represent persistent 
 * <code>javax.util.List</code>-valued attributes.
 *
 * @param <X> The type the represented List belongs to
 * @param <E> The element type of the represented List
 *
<<<<<<< HEAD
 * @since Advanced FineBI Analysis 1.0
=======
 * @since Java Persistence 2.0
>>>>>>> JPA接口
 *
 */
public interface ListAttribute<X, E> 
		extends PluralAttribute<X, java.util.List<E>, E> {}
