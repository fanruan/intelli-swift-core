
package com.finebi.analysis.api.metamodel;

/**
 * Instances of the type <code>MapAttribute</code> represent
 * persistent <code>java.util.Map</code>-valued attributes.
 *
 * @param <X> The type the represented Map belongs to
 * @param <K> The type of the key of the represented Map
 * @param <V> The type of the value of the represented Map
 *
 * @since Java Persistence 2.0
 *
 */
public interface MapAttribute<X, K, V> 
	extends PluralAttribute<X, java.util.Map<K, V>, V> {

    /**
     * Return the Java type of the map key.
     * @return Java key type
     */
    Class<K> getKeyJavaType();

    /**
     * Return the type representing the key type of the map.
     * @return type representing key type
     */
    Type<K> getKeyType();
}
