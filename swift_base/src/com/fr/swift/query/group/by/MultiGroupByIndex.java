package com.fr.swift.query.group.by;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/3/28.
 */
public class MultiGroupByIndex extends MultiGroupBy<int[]> {
    public MultiGroupByIndex(List<Column> dimensions, DetailFilter detailFilter, int[] cursor, boolean[] asc) {
        super(dimensions, detailFilter, cursor, asc);
    }

    @Override
    protected RowIndexKey<int[]> createKey(int[] indexes) {
        return new RowIndexKey<int[]>(indexes);
    }
}
