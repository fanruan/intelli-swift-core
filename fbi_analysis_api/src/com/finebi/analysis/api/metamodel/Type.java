
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>Type</code> represent persistent object 
 * or attribute types.
 *
 * @param <X>  The type of the represented object or attribute
 *
 * @since Advanced FineBI Analysis 1.0
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
