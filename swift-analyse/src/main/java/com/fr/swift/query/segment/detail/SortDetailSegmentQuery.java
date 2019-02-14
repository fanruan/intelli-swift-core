package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.SortSegmentDetailResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by pony on 2017/11/24.
 * 处理排序的明细表
 */
public class SortDetailSegmentQuery extends AbstractDetailSegmentQuery {

    private List<Sort> sorts;

    public SortDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter, List<Sort> sorts) {
        super(fetchSize, columnList, filter);
        this.sorts = sorts;
    }

    @Override
    public QueryResultSet getQueryResult() {
        return new SortSegmentDetailResultSet(fetchSize, columnList, filter, sorts);
    }
}
