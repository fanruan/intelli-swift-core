package com.fr.swift.query.info.element.metric;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.segment.column.ColumnKey;

/**
 * Created by pony on 2017/12/25.
 */
public class GroupMetric extends AbstractMetric {

    private Aggregator aggregator;

    public GroupMetric(int queryIndex, ColumnKey columnKey, FilterInfo filterInfo, Aggregator aggregator) {
        super(queryIndex, columnKey, filterInfo);
        this.aggregator = aggregator;
    }

    @Override
    public Aggregator getAggregator() {
        return aggregator;
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.GROUP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GroupMetric metric = (GroupMetric) o;
        // aggregator是单例的，直接比较引用应该没问题吧
        return aggregator != null ? aggregator.equals(metric.aggregator) : metric.aggregator == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aggregator != null ? aggregator.hashCode() : 0);
        return result;
    }
}
