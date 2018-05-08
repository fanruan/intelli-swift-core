package com.fr.swift.cal.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortSegmentDetailByIndexResultSet;
import com.fr.swift.result.SortSegmentDetailResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;

import java.util.List;

/**
 * Created by pony on 2017/11/24.
 * 处理排序的明细表
 */
public class SortDetailSegmentQuery extends AbstractDetailSegmentQuery {
    private IntList sortIndex;
    private List<SortType> sorts;
    private SwiftMetaData metaData;
    private final static int MEMORY_LIMIT = 3000;

    public SortDetailSegmentQuery(List<Column> columnList, DetailFilter filter, IntList sortIndex, List<SortType> sorts, SwiftMetaData metaData) {
        super(columnList, filter);
        this.sortIndex = sortIndex;
        this.sorts = sorts;
        this.metaData = metaData;
    }

    @Override
    public DetailResultSet getQueryResult() {
        if (filter.createFilterIndex().getCardinality() <= MEMORY_LIMIT) {
            return new SortSegmentDetailResultSet(columnList, filter, sortIndex, sorts, metaData);
        } else {
            return new SortSegmentDetailByIndexResultSet(columnList, filter, sortIndex, sorts, metaData);
        }
    }


}
