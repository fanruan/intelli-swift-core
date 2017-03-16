
package com.finebi.datasource.api.metamodel;

import java.util.Set;

/**
 *  Instances of the type <code>ManagedType</code> represent entity, mapped 
 *  superclass, and embeddable types.
 *
 *  @param <X> The represented type.
 *
 *  @since Advanced FineBI Analysis 1.0
 *
 */
public interface ManagedType<X> extends Type<X> {

    /**
     *  Return the attributes of the managed type.
     *  @return attributes of the managed type
     */
     Set<Attribute<? super X, ?>> getAttributes();

    /**
     *  Return the attributes declared by the managed type.
     *  Returns empty set if the managed type has no declared
     *  attributes.
     *  @return declared attributes of the managed type
     */
     Set<Attribute<X, ?>> getDeclaredAttributes();

    /**
     *  Return the single-valued attribute of the managed 
     *  type that corresponds to the specified name and Java type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return single-valued attribute with given name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type);

    /**
     *  Return the single-valued attribute declared by the 
     *  managed type that corresponds to the specified name and 
     *  Java type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return declared single-valued attribute of the given 
     *          name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type);

    /**
     *  Return the single-valued attributes of the managed type.
     *  Returns empty set if the managed type has no single-valued
     *  attributes.
     *  @return single-valued attributes
     */
    Set<SingularAttribute<? super X, ?>> getSingularAttributes();

    /**
     *  Return the single-valued attributes declared by the managed
     *  type.
     *  Returns empty set if the managed type has no declared
     *  single-valued attributes.
     *  @return declared single-valued attributes
     */
    Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes();




//String-based:

    /**
     *  Return the attribute of the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    Attribute<? super X, ?> getAttribute(String name); 

    /**
     *  Return the attribute declared by the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    Attribute<X, ?> getDeclaredAttribute(String name); 

    /**
     *  Return the single-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return single-valued attribute with the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    SingularAttribute<? super X, ?> getSingularAttribute(String name);

    /**
     *  Return the single-valued attribute declared by the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared single-valued attribute of the given 
     *          name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    SingularAttribute<X, ?> getDeclaredSingularAttribute(String name);







}
