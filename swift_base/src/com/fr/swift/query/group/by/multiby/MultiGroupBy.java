package com.fr.swift.query.group.by.multiby;

import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.List;

/**
 * @author Lyon
 * @date 2018/1/5
 */
public class MultiGroupBy {

    public static InCollector<RowIndexKey, RowTraversal> groupBy(List<Column> dimensions, DetailFilter filter,
                                                                 int[] startIndex, boolean[] asc, boolean showSum,
                                                                 boolean isPaging) {
        GroupByIndexIterable indexIterable = new GroupByIndexIterable(dimensions, filter);
        indexIterable.setAsc(asc);
        indexIterable.setCursor(startIndex);
        indexIterable.setShowSum(showSum);
        return indexIterable;
    }

    public static InCollector<RowIndexKey, RowTraversal> groupByAll(List<Column> dimensions, DetailFilter filter,
                                                                    boolean[] asc, boolean showSum) {
        GroupByIndexIterable indexIterable = new GroupByIndexIterable(dimensions, filter);
        indexIterable.setAsc(asc);
        indexIterable.setShowSum(showSum);
        return indexIterable;
    }

    public static InCollector<RowIndexKey, RowTraversal> groupByAll(List<Column> dimensions, DetailFilter filter) {
        return groupByAll(dimensions, filter, null, false);
    }
}
