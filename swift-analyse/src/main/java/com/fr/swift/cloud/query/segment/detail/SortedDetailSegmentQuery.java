package com.fr.swift.cloud.query.segment.detail;

import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.result.DetailQueryResultSet;
import com.fr.swift.cloud.result.detail.SortSegmentDetailResultSet;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.Pair;

import java.util.List;

/**
 * @author pony
 * @date 2017/11/24
 * 处理排序的明细表
 */
public class SortedDetailSegmentQuery extends AbstractDetailSegmentQuery {

    private List<Sort> sorts;

    public SortedDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter, List<Sort> sorts) {
        super(fetchSize, columnList, filter);
        this.sorts = sorts;
    }

    @Override
    public DetailQueryResultSet getQueryResult() {
        return new SortSegmentDetailResultSet(fetchSize, columnList, filter, sorts);
    }
}
