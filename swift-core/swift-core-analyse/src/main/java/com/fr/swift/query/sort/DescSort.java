package com.fr.swift.query.sort;

import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2018/1/23
 */
public class DescSort extends AbstractSort {

    private static final long serialVersionUID = 8857141913516870459L;
    private SortType sortType = SortType.DESC;

    public DescSort(int targetIndex) {
        super(targetIndex);
    }

    public DescSort(int targetIndex, ColumnKey columnKey) {
        super(targetIndex, columnKey);
    }

    @Override
    public SortType getSortType() {
        return sortType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DescSort descSort = (DescSort) o;

        return sortType == descSort.sortType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sortType != null ? sortType.hashCode() : 0);
        return result;
    }
}
