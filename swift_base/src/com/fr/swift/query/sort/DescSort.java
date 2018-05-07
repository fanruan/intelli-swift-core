package com.fr.swift.query.sort;

import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2018/1/23
 */
public class DescSort extends AbstractSort {
    public DescSort(int targetIndex) {
        super(targetIndex);
    }

    public DescSort(int targetIndex, ColumnKey columnKey) {
        super(targetIndex, columnKey);
    }

    @Override
    public SortType getSortType() {
        return SortType.DESC;
    }
}
