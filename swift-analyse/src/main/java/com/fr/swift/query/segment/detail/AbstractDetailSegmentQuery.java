package com.fr.swift.query.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.segment.AbstractSegmentQuery;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by pony on 2017/11/24.
 */
public abstract class AbstractDetailSegmentQuery extends AbstractSegmentQuery<DetailResultSet> {
    /**
     * 明细的列
     */
    protected List<Pair<Column, IndexInfo>> columnList;
    /**
     * 过滤
     */
    protected DetailFilter filter;

    public AbstractDetailSegmentQuery(List<Pair<Column, IndexInfo>> columnList, DetailFilter filter) {
        this.columnList = columnList;
        this.filter = filter;
    }

}
