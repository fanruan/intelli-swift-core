package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.result.group.GroupPagingResultQuery;
import com.fr.swift.cal.segment.group.GroupPagingSegmentQuery;
import com.fr.swift.cal.targetcal.group.GroupTargetCalQuery;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.manager.LineSegmentManager;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.DimensionInfo;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.group.info.PageGroupByInfo;
import com.fr.swift.query.group.info.PageGroupInfoImpl;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupPagingQueryBuilder extends AbstractLocalGroupQueryBuilder {

    @Override
    public Query<NodeResultSet> buildTargetCalQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info) {
        return new GroupTargetCalQuery(query, info);
    }

    @Override
    public ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
        DimensionInfo dimensionInfo = info.getDimensionInfo();
        TargetInfo targetInfo = info.getTargetInfo();
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        List<Segment> segments = SwiftContext.getInstance().getBean(LineSegmentManager.class).getSegment(info.getTable());
        for (Segment segment : segments) {
            List<Column> rowDimensions = getDimensionSegments(segment, dimensionInfo.getDimensions());
            List<Column> metrics = getMetricSegments(segment, targetInfo.getMetrics());
            List<Aggregator> aggregators = getAggregators(targetInfo.getMetrics());
            DetailFilter rowDetailFilters = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
            List<Sort> rowSorts = LocalGroupAllQueryBuilder.getSegmentIndexSorts(dimensionInfo.getDimensions());
            PageGroupByInfo rowGroupByInfo = new PageGroupInfoImpl(rowDimensions, rowDetailFilters, rowSorts, dimensionInfo.getExpander(), new AllCursor());
            MetricInfo metricInfo = new MetricInfoImpl(metrics, aggregators, targetInfo.getTargetLength());
            queries.add(new GroupPagingSegmentQuery(rowGroupByInfo, metricInfo));
        }
        return new GroupPagingResultQuery(queries, getAggregators(targetInfo.getMetrics()), getTargets(targetInfo.getGroupTargets()));
    }

    @Override
    public ResultQuery<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info) {
        TargetInfo targetInfo = info.getTargetInfo();
        return new GroupPagingResultQuery(queries, getAggregators(targetInfo.getMetrics()), getTargets(targetInfo.getGroupTargets()));
    }
}
