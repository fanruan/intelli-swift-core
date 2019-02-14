package com.fr.swift.query.sort;

import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2018/1/23
 */
public class NoneSort implements Sort {
    private static final long serialVersionUID = -4380716350960072064L;

    @Override
    public SortType getSortType() {
        return SortType.NONE;
    }

    @Override
    public int getTargetIndex() {
        return 0;
    }

    @Override
    public ColumnKey getColumnKey() {
        return null;
    }
}
