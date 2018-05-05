package com.fr.swift.query.adapter.metric;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * Created by Lyon on 2018/5/5.
 */
public class CounterMetric extends AbstractMetric {

    public CounterMetric(int queryIndex, SourceKey sourceKey, ColumnKey columnKey, FilterInfo filterInfo) {
        super(queryIndex, sourceKey, columnKey, filterInfo);
    }

    @Override
    public Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.COUNT);
    }
}
