package com.fr.swift.cal.segment.detail;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SegmentDetailResultSet;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by pony on 2017/11/24.
 * 处理不排序的明细表
 */
public class NormalDetailSegmentQuery extends AbstractDetailSegmentQuery {

    public NormalDetailSegmentQuery(List<Column> columnList, DetailFilter filter) {
        super(columnList, filter);
    }

    @Override
    public DetailResultSet getQueryResult() {
        return new SegmentDetailResultSet(columnList, filter);
    }
}
