package com.fr.swift.mapreduce;

/**
 * Created by Lyon on 18-1-1.
 */
public class KeyValue<K, V> {

    private K key;
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyValue<?, ?> keyValue = (KeyValue<?, ?>) o;

        return key != null ? key.equals(keyValue.key) : keyValue.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
