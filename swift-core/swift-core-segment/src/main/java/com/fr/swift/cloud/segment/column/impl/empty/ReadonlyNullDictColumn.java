package com.fr.swift.cloud.segment.column.impl.empty;

import com.fr.swift.cloud.segment.column.impl.base.AbstractDictColumn;
import com.fr.swift.cloud.source.ColumnTypeConstants.ClassType;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2019/3/26
 */
class ReadonlyNullDictColumn<T> extends AbstractDictColumn<T> {

    private int rowCount;

    private Comparator<T> cmp;

    private ClassType classType;

    protected ReadonlyNullDictColumn(int rowCount, Comparator<T> cmp, ClassType classType) {
        this.rowCount = rowCount;
        this.cmp = cmp;
        this.classType = classType;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int globalSize() {
        return 1;
    }

    @Override
    public T getValue(int index) {
        ReadonlyNullColumn.checkIndex(index, size());
        return null;
    }

    @Override
    public int getIndex(Object value) {
        if (value == null) {
            return 0;
        }
        return -1;
    }

    @Override
    public int getIndexByRow(int row) {
        ReadonlyNullColumn.checkIndex(row, rowCount);
        return 0;
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        ReadonlyNullColumn.checkIndex(index, size());
        return 0;
    }

    @Override
    public Comparator<T> getComparator() {
        return cmp;
    }

    @Override
    public ClassType getType() {
        return classType;
    }

    @Override
    public Putter<T> putter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void release() {
    }

    @Override
    public boolean isReadable() {
        return true;
    }
}