package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.segment.AbstractSegmentQuery;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * @author pony
 * @date 2017/11/24
 */
public abstract class AbstractDetailSegmentQuery extends AbstractSegmentQuery<QueryResultSet> {

    protected int fetchSize;
    protected List<Pair<Column, IndexInfo>> columnList;
    protected DetailFilter filter;

    public AbstractDetailSegmentQuery(int fetchSize, List<Pair<Column, IndexInfo>> columnList, DetailFilter filter) {
        this.fetchSize = fetchSize;
        this.columnList = columnList;
        this.filter = filter;
    }

}
