package com.fr.swift.query.segment.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class ValueCachedOrigineColumn extends OriginDecMergeColumn {
    private List values;

    public ValueCachedOrigineColumn(DictionaryEncodedColumn dic, List values) {
        super(dic);
        this.values = values;
    }

    @Override
    public Object getValue(int row) {
        return values.get(getIndex(row));
    }

    @Override
    public int dictSize() {
        return values.size();
    }
}
