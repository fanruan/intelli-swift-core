package com.fr.swift.query.info.element.metric;

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

    private Aggregator aggregator = AggregatorFactory.createAggregator(AggregatorType.COUNT);

    public CounterMetric(int queryIndex, SourceKey sourceKey, ColumnKey columnKey, FilterInfo filterInfo) {
        super(queryIndex, sourceKey, columnKey, filterInfo);
    }

    @Override
    public Aggregator getAggregator() {
        return aggregator;
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.COUNT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CounterMetric that = (CounterMetric) o;

        return aggregator != null ? aggregator.equals(that.aggregator) : that.aggregator == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aggregator != null ? aggregator.hashCode() : 0);
        return result;
    }
}
