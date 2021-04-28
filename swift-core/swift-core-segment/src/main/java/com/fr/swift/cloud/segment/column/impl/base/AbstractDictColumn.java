package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;

/**
 * @author anchore
 * @date 2018/8/16
 */
public abstract class AbstractDictColumn<T> implements DictionaryEncodedColumn<T> {
    @Override
    public T getValueByRow(int row) {
        return getValue(getIndexByRow(row));
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        return getGlobalIndexByIndex(getIndexByRow(row));
    }
}