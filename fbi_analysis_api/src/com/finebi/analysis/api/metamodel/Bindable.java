
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>Bindable</code> represent object or attribute types 
 * that can be bound into a {@link com.finebi.analysis.api.criteria.Path Path}.
 *
 * @param <T>  The type of the represented object or attribute
 *
<<<<<<< HEAD
 * @since Advanced FineBI Analysis 1.0
=======
 * @since Java Persistence 2.0
>>>>>>> JPA接口
 *
 */
public interface Bindable<T> {
	
	public static enum BindableType { 

	    /** Single-valued attribute type */
	    SINGULAR_ATTRIBUTE, 

	    /** Multi-valued attribute type */
	    PLURAL_ATTRIBUTE, 

	    /** Entity type */
	    ENTITY_TYPE
	}

    /**
     *  Return the bindable type of the represented object.
     *  @return bindable type
     */	
    BindableType getBindableType();
	
    /**
     * Return the Java type of the represented object.
     * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
     * the Java element type is returned. If the bindable type is
     * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>, 
     * the Java type of the
     * represented entity or attribute is returned.
     * @return Java type
     */
    Class<T> getBindableJavaType();
}
