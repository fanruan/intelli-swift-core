package com.fr.swift.query.builder;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.Query;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.post.group.GroupPostQuery;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.query.segment.group.GroupPagingSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupPagingQueryBuilder extends AbstractLocalGroupQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.getInstance().getBean("localSegmentProvider", SwiftSegmentManager.class);

    @Override
    public Query<NodeResultSet> buildPostQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info) {
        return new GroupPostQuery(null, null);
    }

    @Override
    public ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
        List<Dimension> dimensions = info.getDimensions();
        List<Metric> metrics = info.getMetrics();
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegment(info.getTable());
        for (Segment segment : segments) {
            List<Column> dimensionColumns = getDimensionSegments(segment, dimensions);
            List<Column> metricColumns = getMetricSegments(segment, metrics);
            List<Aggregator> aggregators = getAggregators(metrics);
            DetailFilter rowDetailFilters = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
            List<Sort> rowSorts = LocalGroupAllQueryBuilder.getSegmentIndexSorts(dimensions);
            GroupByInfo rowGroupByInfo = new GroupByInfoImpl(dimensionColumns, rowDetailFilters, rowSorts, new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<RowIndexKey<String[]>>()), new AllCursor());
            MetricInfo metricInfo = new MetricInfoImpl(metricColumns, aggregators, metrics.size());
            queries.add(new GroupPagingSegmentQuery(rowGroupByInfo, metricInfo));
        }
//        return new GroupPagingResultQuery(queries, getAggregators(metrics), getTargets(info.getPostCalculationInfo()));
        return null;
    }

    @Override
    public ResultQuery<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info) {
//        return new GroupPagingResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getPostCalculationInfo()));
        return null;
    }
}
