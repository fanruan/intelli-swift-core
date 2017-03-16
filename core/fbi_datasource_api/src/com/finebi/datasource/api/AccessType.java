
package com.finebi.datasource.api;

/**
 * Used with the {@link Access} annotation to specify an access
 * type to be applied to an entity class, mapped superclass, or
 * embeddable class, or to a specific attribute of such a class.
 * 
 * @see Access
 *
 * @since Advanced FineBI Analysis 1.0
 */
public enum AccessType {

    /** Field-based access is used. */
    FIELD,

    /** Property-based access is used. */
    PROPERTY
}
