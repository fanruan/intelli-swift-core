package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.result.SegmentDetailResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 *
 * @author pony
 * @date 2017/11/24
 * 处理不排序的明细表
 */
public class NormalDetailSegmentQuery extends AbstractDetailSegmentQuery {

    public NormalDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter) {
        super(fetchSize, columnList, filter);
    }

    @Override
    public QueryResultSet getQueryResult() {
        return new SegmentDetailResultSet(fetchSize, columnList, filter);
    }
}
