package com.fr.swift.result;

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

    /**
     * equals只比较key！
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyValue<?, ?> keyValue = (KeyValue<?, ?>) o;

        return key != null ? key.equals(keyValue.key) : keyValue.key == null;
    }

    /**
     * hashCode计算只用key！
     * @return
     */
    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "key = " + key.toString();
    }
}
