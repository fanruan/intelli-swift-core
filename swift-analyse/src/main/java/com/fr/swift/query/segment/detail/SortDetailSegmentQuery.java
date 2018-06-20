package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortSegmentDetailResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * Created by pony on 2017/11/24.
 * 处理排序的明细表
 */
public class SortDetailSegmentQuery extends AbstractDetailSegmentQuery {

    private List<Sort> sorts;
    private SwiftMetaData metaData;

    public SortDetailSegmentQuery(List<Column> columnList, DetailFilter filter, List<Sort> sorts, SwiftMetaData metaData) {
        super(columnList, filter);
        this.sorts = sorts;
        this.metaData = metaData;
    }

    @Override
    public DetailResultSet getQueryResult() {
        return new SortSegmentDetailResultSet(columnList, filter, sorts, metaData);
    }
}
