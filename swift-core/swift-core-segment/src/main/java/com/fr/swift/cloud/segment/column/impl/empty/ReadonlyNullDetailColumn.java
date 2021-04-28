package com.fr.swift.cloud.segment.column.impl.empty;

import com.fr.swift.cloud.cube.io.IOConstant;
import com.fr.swift.cloud.segment.column.DetailColumn;

/**
 * @author anchore
 * @date 2019/3/26
 */
class ReadonlyNullDetailColumn<T> implements DetailColumn<T> {
    private int rowCount;

    ReadonlyNullDetailColumn(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public int getInt(int pos) {
        ReadonlyNullColumn.checkIndex(pos, rowCount);
        return IOConstant.NULL_INT;
    }

    @Override
    public long getLong(int pos) {
        ReadonlyNullColumn.checkIndex(pos, rowCount);
        return IOConstant.NULL_LONG;
    }

    @Override
    public double getDouble(int pos) {
        ReadonlyNullColumn.checkIndex(pos, rowCount);
        return IOConstant.NULL_DOUBLE;
    }

    @Override
    public void put(int pos, T val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int pos) {
        ReadonlyNullColumn.checkIndex(pos, rowCount);
        return null;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void release() {
    }
}