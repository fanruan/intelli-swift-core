package com.fr.swift.query.sort;

import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2018/1/23
 */
abstract class AbstractSort implements Sort {
    private int targetIndex;

    private transient ColumnKey columnKey;

    AbstractSort(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    AbstractSort(int targetIndex, ColumnKey columnKey) {
        this.targetIndex = targetIndex;
        this.columnKey = columnKey;
    }

    @Override
    public int getTargetIndex() {
        return targetIndex;
    }

    @Override
    public ColumnKey getColumnKey() {
        return columnKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractSort that = (AbstractSort) o;

        if (targetIndex != that.targetIndex) {
            return false;
        }
        return columnKey != null ? columnKey.equals(that.columnKey) : that.columnKey == null;
    }

    @Override
    public int hashCode() {
        int result = targetIndex;
        result = 31 * result + (columnKey != null ? columnKey.hashCode() : 0);
        return result;
    }
}
