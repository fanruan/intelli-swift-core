package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.query.result.group.GroupResultQuery;
import com.fr.swift.query.segment.group.GroupPagingSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupPagingQueryBuilder extends AbstractLocalGroupQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    @Override
    public ResultQuery<QueryResultSet> buildLocalQuery(GroupQueryInfo info) {
        List<Dimension> dimensions = info.getDimensions();
        List<Metric> metrics = info.getMetrics();
        List<Query<QueryResultSet>> queries = new ArrayList<Query<QueryResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
        for (Segment segment : segments) {
            SwiftLoggers.getLogger().debug("Build Group Segment Query {}", segment.getLocation());
            List<Pair<Column, IndexInfo>> dimensionColumns = getDimensionSegments(segment, dimensions);
            List<Column> metricColumns = getMetricSegments(segment, metrics);
            List<Aggregator> aggregators = LocalGroupAllQueryBuilder.getFilterAggregators(metrics, segment);
            DetailFilter rowDetailFilters = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
            List<Sort> rowSorts = LocalGroupAllQueryBuilder.getSegmentIndexSorts(dimensions);
            GroupByInfo rowGroupByInfo = new GroupByInfoImpl(info.getFetchSize(), dimensionColumns, rowDetailFilters, rowSorts, new AllCursor());
            MetricInfo metricInfo = new MetricInfoImpl(metricColumns, aggregators,
                    metrics.size() + LocalGroupAllQueryBuilder.countCalFields(info.getPostQueryInfoList()));
            queries.add(new GroupPagingSegmentQuery(rowGroupByInfo, metricInfo));
        }
        return new GroupResultQuery(info.getFetchSize(), queries, getAggregators(info.getMetrics()),
                LocalGroupAllQueryBuilder.getComparatorsForMerging(info.getTable(), info.getDimensions()),
                isGlobalIndexed(info.getDimensions()));
    }
}
