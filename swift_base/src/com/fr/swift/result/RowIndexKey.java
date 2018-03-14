package com.fr.swift.result;

import java.util.Arrays;

/**
 * Created by Lyon on 2017/12/29.
 */
public class RowIndexKey {

    private int[] key;

    public RowIndexKey(int[] key) {
        this.key = key;
    }

    public int[] getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RowIndexKey key1 = (RowIndexKey) o;

        return Arrays.equals(key, key1.key);
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
