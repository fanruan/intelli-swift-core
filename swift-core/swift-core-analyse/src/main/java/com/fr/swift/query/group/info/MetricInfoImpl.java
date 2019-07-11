package com.fr.swift.query.group.info;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/4/25.
 */
public class MetricInfoImpl implements MetricInfo {

    private List<Column> metrics;
    private List<Aggregator> aggregators;
    private int targetLength;

    public MetricInfoImpl(List<Column> metrics, List<Aggregator> aggregators, int targetLength) {
        this.metrics = metrics;
        this.aggregators = aggregators;
        this.targetLength = targetLength;
    }

    @Override
    public List<Column> getMetrics() {
        return metrics;
    }

    @Override
    public List<Aggregator> getAggregators() {
        return aggregators;
    }

    @Override
    public int getTargetLength() {
        return targetLength;
    }
}
