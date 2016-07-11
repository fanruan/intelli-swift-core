
package com.finebi.datasource.api;

/**
 * Defines supported types of the discriminator column. 
 *
 * @since Advanced FineBI Analysis 1.0
 */
public enum DiscriminatorType { 

    /** 
     * String as the discriminator type.
     */
    STRING,

    /** 
     * Single character as the discriminator type.
     */
    CHAR,

    /** 
     * Integer as the discriminator type.
     */
    INTEGER
}
