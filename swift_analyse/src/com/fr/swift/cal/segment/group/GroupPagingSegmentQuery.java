package com.fr.swift.cal.segment.group;

import com.fr.swift.cal.info.Expander;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.RowResultCollector;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingSegmentQuery extends AbstractGroupSegmentQuery {
    private Expander expander;

    public GroupPagingSegmentQuery(List<Column> dimensions, List<Column> metrics, List<Aggregator> aggregators, DetailFilter filter, Expander expander) {
        super(dimensions, metrics, aggregators, filter);
        this.expander = expander;
    }

    @Override
    public RowResultCollector getQueryResult() {
        return null;
    }
}
