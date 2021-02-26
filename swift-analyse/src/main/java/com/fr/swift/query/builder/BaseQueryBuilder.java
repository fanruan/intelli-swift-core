package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.funnel.FunnelComplexColumn;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.SingleTableQueryInfo;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.FunnelPathsMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.segment.CommonSegmentFilter;
import com.fr.swift.query.info.segment.FunnelSegmentFilter;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2019/6/27
 */
class BaseQueryBuilder {
    protected static final SegmentService SEG_SVC = SwiftContext.get().getBean(SegmentService.class);
    protected static final SwiftTableAllotRuleService ALLOT_RULE_SERVICE = SwiftContext.get().getBean(SwiftTableAllotRuleService.class);

    static List<Segment> filterQuerySegs(SingleTableQueryInfo queryInfo) throws SwiftMetaDataException {
        List<SegmentKey> segmentKeyList = filterQuerySegKeys(queryInfo);
//        return segmentKeyList.stream().map(SEG_SVC::getSegment).collect(Collectors.toList());
        return new QuerySegmentFilter().getDetailSegment(queryInfo.getFilterInfo(), segmentKeyList);
    }

    static List<SegmentKey> filterQuerySegKeys(SingleTableQueryInfo queryInfo) throws SwiftMetaDataException {
        SourceKey table = queryInfo.getTable();
        SwiftTableAllotRule allotRule = ALLOT_RULE_SERVICE.getByTale(table);
        SwiftSegmentBucket swiftSegmentBucket = SEG_SVC.getBucketByTable(table);
        if (queryInfo.getType() == QueryType.GROUP) {
            GroupQueryInfo groupQueryInfo = (GroupQueryInfo) queryInfo;
            List<Metric> metrics = groupQueryInfo.getMetrics();
            if (metrics.size() != 0) {
                for (Metric metric : metrics) {
                    if (metric.getMetricType() == MetricType.FUNNEL || metric.getMetricType() == MetricType.FUNNEL_PATHS) {
                        //漏斗计算
                        return new FunnelSegmentFilter(allotRule, swiftSegmentBucket).filterSegKeys(queryInfo);
                    }
                }
            }
        }
        return new CommonSegmentFilter(allotRule, swiftSegmentBucket).filterSegKeys(queryInfo);
    }

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

    static List<Column> getMetricSegments(Segment segment, List<Metric> metrics) {
        List<Column> metricColumns = new ArrayList<Column>();
        for (Metric metric : metrics) {
            switch (metric.getMetricType()) {
                case FUNNEL:
                case FUNNEL_PATHS:
                    Map<ColumnKey, Column<?>> columnMap = new HashMap<ColumnKey, Column<?>>();
                    FunnelPathsMetric pathsMetric = (FunnelPathsMetric) metric;
                    for (ColumnKey columnKey : pathsMetric.getColumnKeys()) {
                        columnMap.put(columnKey, segment.getColumn(columnKey));
                    }
                    metricColumns.add(new FunnelComplexColumn(pathsMetric.getColumn(segment), columnMap));
                    break;
                default:
                    metricColumns.add(metric.getColumn(segment));
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