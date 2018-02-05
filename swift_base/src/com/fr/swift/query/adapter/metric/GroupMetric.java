package com.fr.swift.query.adapter.metric;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2017/12/25.
 */
public class GroupMetric extends AbstractMetric {
    private Aggregator aggregator;
    public GroupMetric(int index, SourceKey sourceKey, ColumnKey columnKey, FilterInfo filterInfo, Aggregator aggregator) {
        super(index, sourceKey, columnKey, filterInfo);
        this.aggregator = aggregator;
    }

    @Override
    public Aggregator getAggregator() {
        return aggregator;
    }
}
