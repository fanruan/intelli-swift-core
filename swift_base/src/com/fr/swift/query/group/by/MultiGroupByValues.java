package com.fr.swift.query.group.by;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/3/28.
 */
public class MultiGroupByValues extends MultiGroupBy<Object[]> {

    public MultiGroupByValues(List<Column> dimensions, DetailFilter detailFilter, int[] cursor, boolean[] asc) {
        super(dimensions, detailFilter, cursor, asc);
    }

    @Override
    protected RowIndexKey<Object[]> createKey(int[] indexes) {
        Object[] values = new Object[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            values[i] = dimensions.get(i).getDictionaryEncodedColumn().getValue(indexes[i]);
        }
        return new RowIndexKey<Object[]>(values);
    }
}
