package com.fr.swift.cal.segment.group;

import com.fr.swift.cal.segment.AbstractSegmentQuery;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.RowResultCollector;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by pony on 2017/12/18.
 */
public abstract class AbstractGroupSegmentQuery extends AbstractSegmentQuery<RowResultCollector> {
    protected List<Column> dimensions;
    protected List<Column> metrics;
    protected List<Aggregator> aggregators;
    protected DetailFilter filter;
    // 这边要加一个维度排序
    protected List<Sort> indexSorts;

    public AbstractGroupSegmentQuery(List<Column> dimensions, List<Column> metrics, List<Aggregator> aggregators, DetailFilter filter) {
        this.dimensions = dimensions;
        this.metrics = metrics;
        this.aggregators = aggregators;
        this.filter = filter;
    }

}
