package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.compare.Comparators;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.funnel.FunnelComplexColumn;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
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
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.exception.LambdaWrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        return getDetailSegment(queryInfo.getFilterInfo(), segmentKeyList);
    }

    /**
     * 查询前优化，根据查询的信息来进行块的过滤
     *
     * @param filterInfo     过滤信息
     * @param segmentKeyList 块列表
     * @return 过滤好的块
     * @throws SwiftMetaDataException
     */
    static List<Segment> getDetailSegment(FilterInfo filterInfo, List<SegmentKey> segmentKeyList) throws SwiftMetaDataException {
        Map<String, Segment> idSegments = new HashMap<>();
        segmentKeyList.forEach(segmentKey -> idSegments.put(segmentKey.getId(), SEG_SVC.getSegment(segmentKey)));
        Set<String> strings = dfsSearch(filterInfo, idSegments);
        return strings.stream().map(idSegments::get).collect(Collectors.toList());
    }

    static Set<String> dfsSearch(FilterInfo filterInfo, Map<String, Segment> idSegments) throws SwiftMetaDataException {
        if (filterInfo instanceof GeneralFilterInfo) {
            GeneralFilterInfo generalFilterInfo = (GeneralFilterInfo) filterInfo;
            List<FilterInfo> childrenFilterInfoList = generalFilterInfo.getChildren();
            if (generalFilterInfo.getType() == GeneralFilterInfo.OR) {
                // 或判断必须遍历完成
                Set<String> set = null;
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<String> subIndexSet = dfsSearch(filter, idSegments);
                    if (set == null) {
                        set = subIndexSet;
                    } else {
                        set.addAll(dfsSearch(filter, idSegments));
                    }
                }
                return set;
            } else {
                Set<String> set = null;
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<String> subIndexSet = dfsSearch(filter, idSegments);
                    if (set == null) {
                        set = subIndexSet;
                    } else {
                        set.retainAll(subIndexSet);
                    }
                    // 与判断的提前终止
                    if (set.isEmpty()) {
                        return set;
                    }
                }
                return set;
            }
        } else if (filterInfo instanceof SwiftDetailFilterInfo) {
            SwiftDetailFilterInfo detailFilterInfo = (SwiftDetailFilterInfo) filterInfo;
            return idSegments.entrySet().stream()
                    .filter(LambdaWrapper.rethrowPredicate(stringSegmentEntry -> canAdd(detailFilterInfo, stringSegmentEntry.getValue())))
                    .map(Map.Entry::getKey).collect(Collectors.toSet());
        }
        return idSegments.keySet();
    }


    static boolean canAdd(SwiftDetailFilterInfo detailFilterInfo, Segment segment) throws SwiftMetaDataException {
        // todo::简易版，后续补充和优化
        switch (detailFilterInfo.getType()) {
            // 利用字典值有序的条件，在in这种情况下，过滤的值一定大于或等于字典第一个值，小于或等于最后一个值，任意一个满足条件即可，时间复杂度O(n),n为filter取值的个数
            case IN: {
                ColumnKey columnKey = detailFilterInfo.getColumnKey();
                Column column = segment.getColumn(columnKey);
                DictionaryEncodedColumn dictionaryEncodedColumn = column.getDictionaryEncodedColumn();
                SwiftMetaDataColumn metaDataColumn = segment.getMetaData().getColumn(columnKey.getName());
                Comparator asc = getComparator(ColumnTypeUtils.getClassType(metaDataColumn));
                Set setValues = (Set) detailFilterInfo.getFilterValue();
                Object start = dictionaryEncodedColumn.getValue(1);
                Object end = dictionaryEncodedColumn.getValue(dictionaryEncodedColumn.size() - 1);
                for (Object tempValue : setValues) {
                    if (asc.compare(tempValue, start) >= 0 && asc.compare(tempValue, end) <= 0) {
                        return true;
                    }
                }
                return false;
            }
            case STRING_LIKE:
            case STRING_STARTS_WITH:
            case STRING_ENDS_WITH:
            case STRING_LIKE_IGNORE_CASE:
            case NUMBER_IN_RANGE:
            case NUMBER_AVERAGE:
            case TOP_N:
            case BOTTOM_N:
            case NULL:
            case FORMULA:
            case KEY_WORDS:
            case EMPTY:
            case WORK_DAY:
            default:
                return true;
        }
    }

    private static Comparator<?> getComparator(ColumnTypeConstants.ClassType classType) {
        switch (classType) {
            case INTEGER:
            case LONG:
            case DATE:
            case DOUBLE:
                return Comparators.asc();
            case STRING:
                return Comparators.STRING_ASC;
            default:
                throw new IllegalStateException(String.format("unsupported type %s", classType));
        }
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