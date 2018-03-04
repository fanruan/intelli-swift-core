package com.fr.swift.result;

import java.util.Arrays;

/**
 * Created by Lyon on 2017/12/29.
 */
public class RowIndexKey {

    private int[] indexes;
    private boolean isSum = false;

    public RowIndexKey(int length, boolean isSum) {
        indexes = new int[length];
        this.isSum = isSum;
        Arrays.fill(indexes, -1);
    }

    public RowIndexKey(int[] indexes, boolean isSum) {
        this.indexes = indexes;
        this.isSum = isSum;
    }

    public void setValue(int index, int value) {
        indexes[index] = value;
    }

    public void setValues(int[] values) {
        this.indexes = values;
    }

    public int getValue(int index) {
        return indexes[index];
    }

    public int[] getValues() {
        return indexes;
    }

    public boolean isSum() {
        return isSum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowIndexKey that = (RowIndexKey) o;

        if (isSum != that.isSum) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(indexes, that.indexes);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(indexes);
        result = 31 * result + (isSum ? 1 : 0);
        return result;
    }

    @Override
    public Object clone() {
        RowIndexKey key = new RowIndexKey(indexes.length, isSum);
        key.setValues(Arrays.copyOf(indexes, indexes.length));
        return key;
    }
}
