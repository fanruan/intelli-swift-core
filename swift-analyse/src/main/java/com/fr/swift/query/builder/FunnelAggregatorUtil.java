package com.fr.swift.query.builder;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.FunnelAggregator;
import com.fr.swift.query.aggregator.FunnelPathsAggregator;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.query.info.element.metric.FunnelMetric;
import com.fr.swift.query.info.element.metric.FunnelPathsMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-11
 */
public class FunnelAggregatorUtil {
    public static Aggregator handleFunnelAggregator(Metric metric, Segment segment) {
        switch (metric.getMetricType()) {
            case FUNNEL_PATHS:
                FunnelPathsAggregator funnelPath = (FunnelPathsAggregator) metric.getAggregator();
                FunnelPathsMetric funnelPathsMetric = (FunnelPathsMetric) metric;
                funnelPath.setEventFilters(funnelPathsMetric.getEventFilter(segment));
                return funnelPath;
            case FUNNEL:
                FunnelAggregator funnel = (FunnelAggregator) metric.getAggregator();
                FunnelMetric funnelMetric = (FunnelMetric) metric;
                funnel.setEventFilters(funnelMetric.getEventFilter(segment));
                funnel.setTimeGroupFilter(funnelMetric.getTimeGroupFilter(new SourceKey(segment.getMetaData().getId())));
                return funnel;
            default:
                return metric.getAggregator();
        }
    }

    public static List<Pair<SortType, ColumnTypeConstants.ClassType>> createMetricComparatorsForMerging(List<Metric> metrics) {
        List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators = new ArrayList<Pair<SortType, ColumnTypeConstants.ClassType>>();
        for (Metric metric : metrics) {
            if (metric.getMetricType() == MetricType.FUNNEL) {
                FunnelMetric funnelMetric = (FunnelMetric) metric;
                comparators.add(Pair.of(SortType.ASC, ColumnTypeConstants.ClassType.STRING));
                if (funnelMetric.isPostGroup()) {
                    comparators.add(Pair.of(SortType.ASC, ColumnTypeConstants.ClassType.STRING));
                }
            } else if (metric.getMetricType() == MetricType.FUNNEL_PATHS) {
                comparators.add(Pair.of(SortType.ASC, ColumnTypeConstants.ClassType.STRING));
            } else {
                // do nothing
            }
        }
        return comparators;
    }
}
