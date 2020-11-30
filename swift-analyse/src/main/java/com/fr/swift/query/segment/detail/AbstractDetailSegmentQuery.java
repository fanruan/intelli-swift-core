package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.limit.Limit;
import com.fr.swift.query.segment.AbstractSegmentQuery;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

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
