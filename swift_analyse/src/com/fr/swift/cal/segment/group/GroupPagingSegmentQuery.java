package com.fr.swift.cal.segment.group;

import com.fr.swift.cal.info.Expander;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.GroupByUtils;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.segment.column.Column;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingSegmentQuery extends AbstractGroupSegmentQuery {
    private Expander expander;
    private int pageSize;

    public GroupPagingSegmentQuery(List<Column> dimensions, List<Column> metrics, List<Aggregator> aggregators, DetailFilter filter, Expander expander) {
        super(dimensions, metrics, aggregators, filter);
        this.expander = expander;
    }

    @Override
    public GroupByResultSet getQueryResult() {
        return GroupByUtils.query(dimensions, metrics, aggregators, filter, indexSorts, createCursor(), pageSize);
    }

    private int[] createCursor() {
        // TODO: 2018/3/3 convert expander to cursor
        int[] cursor = new int[dimensions.size()];
        Arrays.fill(cursor, 0);
        return cursor;
    }
}
