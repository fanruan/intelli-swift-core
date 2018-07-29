package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortSegmentDetailResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by pony on 2017/11/24.
 * 处理排序的明细表
 */
public class SortDetailSegmentQuery extends AbstractDetailSegmentQuery {

    private List<Sort> sorts;
    private SwiftMetaData metaData;

    public SortDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter, List<Sort> sorts, SwiftMetaData metaData) {
        super(fetchSize, columnList, filter);
        this.sorts = sorts;
        this.metaData = metaData;
    }

    @Override
    public DetailResultSet getQueryResult() {
        return new SortSegmentDetailResultSet(fetchSize, columnList, filter, sorts, metaData);
    }
}
