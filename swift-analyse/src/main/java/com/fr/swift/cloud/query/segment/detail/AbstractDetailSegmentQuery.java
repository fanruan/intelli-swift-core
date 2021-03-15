package com.fr.swift.cloud.query.segment.detail;

import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.query.segment.AbstractSegmentQuery;
import com.fr.swift.cloud.result.DetailQueryResultSet;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.Pair;

import java.util.List;

/**
 * @author pony
 * @date 2017/11/24
 */
abstract class AbstractDetailSegmentQuery extends AbstractSegmentQuery<DetailQueryResultSet> {

    int fetchSize;
    List<Pair<Column, IndexInfo>> columnList;
    DetailFilter filter;
    Limit limit;

    AbstractDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter) {
        this(fetchSize, columnList, filter, null);
    }

    AbstractDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter, Limit limit) {
        this.fetchSize = fetchSize;
        this.columnList = columnList;
        this.filter = filter;
        this.limit = limit;
    }

}
