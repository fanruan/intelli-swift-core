package com.fr.swift.query.aggregator.funnel;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * @author yee
 * @date 2019-08-29
 */
public class SingleAssociatedColumnImpl implements AssociatedColumn {
    private DictionaryEncodedColumn column;

    public SingleAssociatedColumnImpl(DictionaryEncodedColumn column) {
        this.column = column;
    }

    @Override
    public int getIndex(int columnIdx, int row) {
        return column.getIndexByRow(row);
    }

    @Override
    public int dictSize() {
        return column.size();
    }
}
