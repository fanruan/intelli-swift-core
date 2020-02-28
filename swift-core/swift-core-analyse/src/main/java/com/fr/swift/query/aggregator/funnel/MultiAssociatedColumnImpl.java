package com.fr.swift.query.aggregator.funnel;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2019-08-29
 */
public class MultiAssociatedColumnImpl implements AssociatedColumn {
    private Map<Integer, DictionaryEncodedColumn> columns;
    private Map<Object, Integer> idx;

    public MultiAssociatedColumnImpl(Map<Integer, DictionaryEncodedColumn> columns) {
        this.columns = columns;
        this.idx = new HashMap<>();
        this.idx.put(null, 0);
        for (DictionaryEncodedColumn column : columns.values()) {
            for (int i = 1; i < column.size(); i++) {
                final Object value = column.getValue(i);
                if (!idx.containsKey(value)) {
                    idx.put(value, idx.size());
                }
            }
        }
    }

    @Override
    public int getIndex(int columnIdx, int row) {
        if (!columns.containsKey(columnIdx)) {
            return -1;
        }
        final Object valueByRow = columns.get(columnIdx).getValueByRow(row);
        return idx.get(valueByRow);
    }

    @Override
    public int dictSize() {
        return this.idx.size();
    }
}
