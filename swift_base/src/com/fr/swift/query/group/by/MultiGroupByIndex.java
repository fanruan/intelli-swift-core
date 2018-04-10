package com.fr.swift.query.group.by;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/3/28.
 */
public class MultiGroupByIndex extends MultiGroupBy<int[]> {

    private boolean isGlobalKey = false;

    /**
     * 单个segment使用的迭代器
     */
    public MultiGroupByIndex(List<Column> dimensions, DetailFilter detailFilter, int[] cursor, boolean[] asc) {
        super(dimensions, detailFilter, cursor, asc);
    }

    /**
     * 如果迭代器用于多个segment之间的合并，isGlobalKey要设为true
     */
    public MultiGroupByIndex(List<Column> dimensions, DetailFilter detailFilter,
                             int[] cursor, boolean[] asc, boolean isGlobalKey) {
        super(dimensions, detailFilter, cursor, asc);
        this.isGlobalKey = isGlobalKey;
    }

    @Override
    protected RowIndexKey<int[]> createKey(int[] indexes) {
        if (isGlobalKey) {
            int[] globalIndex = new int[indexes.length];
            for (int i = 0; i < indexes.length; i++) {
                globalIndex[i] = dimensions.get(i).getDictionaryEncodedColumn().getGlobalIndexByIndex(indexes[i]);
            }
            return new RowIndexKey<int[]>(globalIndex);
        }
        return new RowIndexKey<int[]>(Arrays.copyOf(indexes, indexes.length));
    }
}
