
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>Type</code> represent persistent object 
 * or attribute types.
 *
 * @param <X>  The type of the represented object or attribute
 *
<<<<<<< HEAD
 * @since Advanced FineBI Analysis 1.0
=======
 * @since Java Persistence 2.0
>>>>>>> JPA接口
 */
public interface Type<X> {

       public static enum PersistenceType {

	   /** Entity */
           ENTITY, 

	   /** Embeddable class */
	   EMBEDDABLE, 

	   /** Mapped superclass */
	   MAPPED_SUPERCLASS, 

	   /** Basic type */
	   BASIC
       }

    /**
     *  Return the persistence type.
     *  @return persistence type
     */	
    PersistenceType getPersistenceType();

    /**
     *  Return the represented Java type.
     *  @return Java type
     */
    Class<X> getJavaType();
}
