
package com.finebi.datasource.api.metamodel;

/**
 * Represents an attribute of a Java type.
 *
 * @param <X> The represented type that contains the attribute
 * @param <Y> The type of the represented attribute
 * @since Advanced FineBI Analysis 1.0
 */
public interface Attribute<X, Y> {


    /**
     * Return the name of the attribute.
     *
     * @return name
     */
    String getName();


    /**
     * Return the managed type representing the type in which
     * the attribute was declared.
     *
     * @return declaring type
     */
    ManagedType<X> getOwnerType();

    /**
     * Return the Java type of the represented attribute.
     *
     * @return Java type
     */
    Class<Y> getJavaType();


    /**
     * Is the attribute an association.
     *
     * @return boolean indicating whether the attribute
     * corresponds to an association
     */
    boolean isAssociation();


}
