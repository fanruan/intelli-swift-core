package com.fr.swift.query.sort;

import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2018/1/23
 */
abstract class AbstractSort implements Sort {
    private int targetIndex;

    private ColumnKey columnKey;
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
}
