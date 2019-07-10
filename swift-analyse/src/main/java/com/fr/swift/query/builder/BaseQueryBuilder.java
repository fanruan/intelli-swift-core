package com.fr.swift.query.builder;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.FunnelPathsMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2019/6/27
 */
class BaseQueryBuilder {

    static boolean[] isGlobalIndexed(List<Dimension> dimensions, List<Metric> metrics) {
        boolean[] booleans = new boolean[dimensions.size()];
        for (int i = 0; i < dimensions.size(); i++) {
            booleans[i] = dimensions.get(i).getIndexInfo().isGlobalIndexed();
        }
        for (Metric metric : metrics) {
            switch (metric.getMetricType()) {
                case FUNNEL:
                case FUNNEL_PATHS:
                    boolean[] target = new boolean[booleans.length + 1];
                    System.arraycopy(booleans, 0, target, 0, booleans.length);
                    target[booleans.length] = false;
                    booleans = target;
                    break;
                default:
            }
        }
        return booleans;
    }

    static List<Pair<Column, IndexInfo>> getDimensionSegments(Segment segment, List<Dimension> dimensions) {
        List<Pair<Column, IndexInfo>> dimensionColumns = new ArrayList<Pair<Column, IndexInfo>>();
        for (Dimension dimension : dimensions) {
            List<Column> columnList = new ArrayList<Column>();
            Column column = dimension.getColumn(segment);
            columnList.add(column);
            Group group = dimension.getGroup();
            GroupOperator operator = null;
            Sort sort = dimension.getSort();
            if (sort != null && sort.getColumnKey() != null) {
                columnList.add(segment.getColumn(sort.getColumnKey()));
            }
            if (group != null) {
                operator = group.getGroupOperator();
            }
            if (operator != null) {
                column = operator.group(columnList);
            }
            dimensionColumns.add(Pair.of(column, dimension.getIndexInfo()));
        }
        return dimensionColumns;
    }

    static List<Map<ColumnKey, Column>> getMetricSegments(Segment segment, List<Metric> metrics) {
        List<Map<ColumnKey, Column>> metricColumns = new ArrayList<Map<ColumnKey, Column>>();
        for (Metric metric : metrics) {
            switch (metric.getMetricType()) {
                case FUNNEL:
                case FUNNEL_PATHS:
                    Map<ColumnKey, Column> columnMap = new HashMap<ColumnKey, Column>();
                    FunnelPathsMetric pathsMetric = (FunnelPathsMetric) metric;
                    for (ColumnKey columnKey : pathsMetric.getColumnKeys()) {
                        columnMap.put(columnKey, segment.getColumn(columnKey));
                    }
                    metricColumns.add(columnMap);
                    break;
                default:
                    metricColumns.add(Collections.singletonMap((ColumnKey) null, metric.getColumn(segment)));
            }


        }
        return metricColumns;
    }

    static List<Aggregator> getAggregators(List<Metric> metrics) {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        for (Metric metric : metrics) {
            aggregators.add(metric.getAggregator());
        }
        return aggregators;
    }

    static List<GroupTarget> getTargets(List<GroupTarget> targets) {
        List<GroupTarget> targetList = new ArrayList<GroupTarget>();
        if (targets != null) {
            targetList.addAll(targets);
        }
        return targetList;
    }
}