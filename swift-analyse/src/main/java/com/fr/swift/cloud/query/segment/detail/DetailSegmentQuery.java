package com.fr.swift.cloud.query.segment.detail;

import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.result.DetailQueryResultSet;
import com.fr.swift.cloud.result.detail.SegmentDetailResultSet;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.Pair;

import java.util.List;

/**
 * @author pony
 * @date 2017/11/24
 * 处理不排序的明细表
 */
public class DetailSegmentQuery extends AbstractDetailSegmentQuery {

    public DetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter, Limit limit) {
        super(fetchSize, columnList, filter, limit);
    }

    @Override
    public DetailQueryResultSet getQueryResult() {
        return new SegmentDetailResultSet(fetchSize, columnList, filter, limit);
    }
}
