package com.fr.swift.structure;

import java.io.Serializable;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/1/2
 */
public class Pair<K, V> implements Entry<K, V>, Serializable {

    private static final long serialVersionUID = 4248136150831910862L;

    private K key;

    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V val) {
        return new Pair<K, V>(key, val);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (key != null ? !key.equals(pair.key) : pair.key != null) {
            return false;
        }
        return value != null ? value.equals(pair.value) : pair.value == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}