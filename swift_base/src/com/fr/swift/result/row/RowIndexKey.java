package com.fr.swift.result.row;

import com.fr.swift.util.Util;

import java.util.Arrays;

/**
 * Created by Lyon on 2017/12/29.
 */
public class RowIndexKey<T> {

    private RowIndexKey rowIndexKey;

    public RowIndexKey(int[] key) {
        Util.requireNonNull(key);
        this.rowIndexKey = new IntKey(key);
    }

    public RowIndexKey(Object[] key) {
        //不能用Util.requireNonNull，这边只要数组不是null，不要求元素不是null
        if (key == null){
            throw new NullPointerException();
        }
        this.rowIndexKey = new ObjectKey(key);
    }

    RowIndexKey() {
    }

    public T getKey() {
        return (T) rowIndexKey.getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return rowIndexKey.equals(o);
    }

    @Override
    public int hashCode() {
        return rowIndexKey.hashCode();
    }

    @Override
    public String toString() {
        return rowIndexKey.toString();
    }
}

class IntKey extends RowIndexKey {

    protected int[] key;

    public IntKey(int[] key) {
        this.key = key;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        RowIndexKey key1 = (RowIndexKey) o;

        return Arrays.equals(key, (int[]) key1.getKey());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }

    @Override
    public String toString() {
        return Arrays.toString(key);
    }
}

class ObjectKey extends RowIndexKey<Object[]> {

    private Object[] key;

    public ObjectKey(Object[] key) {
        this.key = key;
    }

    @Override
    public Object[] getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        RowIndexKey key1 = (RowIndexKey) o;

        return Arrays.equals(key, (Object[]) key1.getKey());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }

    @Override
    public String toString() {
        return Arrays.toString(key);
    }
}
