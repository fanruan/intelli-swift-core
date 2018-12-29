package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.group.GroupResultQuery;
import com.fr.swift.query.segment.group.GroupAllSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupAllQueryBuilder extends AbstractLocalGroupQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    @Override
    public <T extends QueryResultSet> Query<T> buildLocalQuery(GroupQueryInfo info) {
        List<Query<QueryResultSet>> queries = new ArrayList<Query<QueryResultSet>>();
        List<Metric> metrics = info.getMetrics();
        List<Dimension> dimensions = info.getDimensions();
        List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
        for (Segment segment : segments) {
            List<Pair<Column, IndexInfo>> dimensionColumns = getDimensionSegments(segment, dimensions);
            List<Column> metricColumns = getMetricSegments(segment, metrics);
            List<Aggregator> aggregators = getFilterAggregators(metrics, segment);
            List<Sort> rowIndexSorts = getSegmentIndexSorts(dimensions);
            DetailFilter rowDetailFilter = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
            GroupByInfo rowGroupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensionColumns, rowDetailFilter, rowIndexSorts, null);
            MetricInfo metricInfo = new MetricInfoImpl(metricColumns, aggregators,
                    metrics.size() + countCalFields(info.getPostQueryInfoList()));
            queries.add(new GroupAllSegmentQuery(rowGroupByInfo, metricInfo));
        }
        return (Query<T>) new GroupResultQuery(info.getFetchSize(), queries, getAggregators(metrics),
                getComparatorsForMerging(info.getTable(), dimensions), isGlobalIndexed(info.getDimensions()));
    }

    static int countCalFields(List<PostQueryInfo> postQueryInfoList) {
        int count = 0;
        for (PostQueryInfo postQueryInfo : postQueryInfoList) {
            if (postQueryInfo.getType() == PostQueryType.CAL_FIELD) {
                count += 1;
            }
        }
        return count;
    }

    static List<Aggregator> getFilterAggregators(List<Metric> metrics, Segment segment) {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        for (Metric metric : metrics){
            if (metric.getFilter() != null){
                aggregators.add(new MetricFilterAggregator(metric.getAggregator(), FilterBuilder.buildDetailFilter(segment, metric.getFilter())));
            } else {
                aggregators.add(metric.getAggregator());
            }
        }
        return aggregators;
    }

    /**
     * 维度的明细排序，按照维度值的字典排序
     */
    static List<Sort> getSegmentIndexSorts(List<Dimension> dimensions) {
        List<Sort> indexSorts = new ArrayList<Sort>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() == dimension.getIndex()) {
                indexSorts.add(sort);
            }
        }
        return indexSorts;
    }

    static List<Pair<SortType, ColumnTypeConstants.ClassType>> getComparatorsForMerging(SourceKey table, List<Dimension> dimensions) {
        SwiftMetaData metaData = SwiftDatabase.getInstance().getTable(table).getMetadata();
        List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators = new ArrayList<Pair<SortType, ColumnTypeConstants.ClassType>>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            SortType type = sort == null || sort.getSortType() == SortType.ASC ? SortType.ASC : SortType.DESC;
            if (dimension.getIndexInfo().isGlobalIndexed()) {
                comparators.add(Pair.of(type, ColumnTypeConstants.ClassType.INTEGER));
            } else {
                comparators.add(Pair.of(type, getComparatorByColumn(metaData, dimension.getColumnKey().getName())));
            }
        }
        return comparators;
    }

    private static ColumnTypeConstants.ClassType getComparatorByColumn(SwiftMetaData metaData, String columnName) {
        SwiftMetaDataColumn column = null;
        try {
            column = metaData.getColumn(columnName);
        } catch (SQLException e) {
            Crasher.crash("failed to read metadata of table: " + metaData.toString());
        }
        return ColumnTypeUtils.getClassType(column);
    }
}
