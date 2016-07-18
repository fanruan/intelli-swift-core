
package com.finebi.datasource.api;

/**
 * Defines mapping for enumerated types.  The constants of this
 * enumerated type specify how a persistent property or
 * field of an enumerated type should be persisted.
 * 
 * @since Advanced FineBI Analysis 1.0
 */
public enum EnumType {
    /** Persist enumerated type property or field as an integer. */
    ORDINAL,

    /** Persist enumerated type property or field as a string. */
    STRING
}
