package com.fr.swift.cal.segment.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.RowResultCollector;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by pony on 2017/12/18.
 */
public class GroupAllSegmentQuery extends AbstractGroupSegmentQuery{

    public GroupAllSegmentQuery(List<Column> dimensions, List<Column> metrics, List<Aggregator> aggregators, DetailFilter filter) {
        super(dimensions, metrics, aggregators, filter);
    }

    @Override
    public RowResultCollector getQueryResult() {
        return SegmentQueryUtils.queryAll(dimensions, metrics, aggregators, filter);
    }
}
