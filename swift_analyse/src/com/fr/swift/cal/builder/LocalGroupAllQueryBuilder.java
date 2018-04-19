package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.cal.result.group.GroupResultQuery;
import com.fr.swift.cal.result.group.XGroupResultQuery;
import com.fr.swift.cal.segment.group.GroupAllSegmentQuery;
import com.fr.swift.cal.segment.group.XGroupAllSegmentQuery;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupAllQueryBuilder extends AbstractLocalGroupQueryBuilder {


    @Override
    public Query<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        QueryType type = info.getType();
            List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(info.getTable());
            for (Segment segment : segments) {
                List<Column> dimensionSegments = getDimensionSegments(segment, info.getDimensions());
                List<Column> metricSegments = getMetricSegments(segment, info.getMetrics());
                List<Aggregator> aggregators = getFilterAggregators(info.getMetrics(), segment);
                List<Sort> rowIndexSorts = getSegmentIndexSorts(info.getDimensions());
                if (type == QueryType.CROSS_GROUP) {
                    List<Column> colDimension = getDimensionSegments(segment, ((XGroupQueryInfo) info).getColDimensions());
                    List<Sort> colIndexSorts = getSegmentIndexSorts(((XGroupQueryInfo) info).getColDimensions());
                    queries.add(new XGroupAllSegmentQuery(dimensionSegments, colDimension, metricSegments, aggregators,
                            FilterBuilder.buildDetailFilter(segment, info.getFilterInfo()), rowIndexSorts, colIndexSorts));
                } else {
                    queries.add(new GroupAllSegmentQuery(dimensionSegments, metricSegments, aggregators,
                            FilterBuilder.buildDetailFilter(segment, info.getFilterInfo()), rowIndexSorts));
                }
            }
        if (type == QueryType.CROSS_GROUP) {
            return new XGroupResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()));
        }
        return new GroupResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()));

    }

    private List<Aggregator> getFilterAggregators(Metric[] metrics, Segment segment) {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        for (Metric metric : metrics){
            if (metric.getFilter() != null){
                aggregators.add(new MetricFilterAggregator(metric.getAggregator(), FilterBuilder.buildDetailFilter(segment, metric.getFilter()).createFilterIndex()));
            } else {
                aggregators.add(metric.getAggregator());
            }
        }
        return aggregators;
    }

    @Override
    public Query<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info) {
        QueryType type = info.getType();
        if (type == QueryType.CROSS_GROUP) {
            return new XGroupResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()), getIndexSorts(info.getDimensions()), getDimensionMatchFilters(info.getDimensions()));
        }
        return new GroupResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()), getIndexSorts(info.getDimensions()), getDimensionMatchFilters(info.getDimensions()));
    }

    /**
     * 维度的明细排序，按照维度值的字典排序
     */
    private List<Sort> getSegmentIndexSorts(Dimension[] dimensions) {
        List<Sort> indexSorts = new ArrayList<Sort>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() == dimension.getIndex()) {
                indexSorts.add(sort);
            }
        }
        return indexSorts;
    }

    /**
     * 维度根据结果（比如聚合之后的指标）排序
     */
    private List<Sort> getIndexSorts(Dimension[] dimensions) {
        List<Sort> indexSorts = new ArrayList<Sort>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() != dimension.getIndex()) {
                indexSorts.add(sort);
            }
        }
        return indexSorts;
    }

    public List<MatchFilter> getDimensionMatchFilters(Dimension[] dimensions) {
        List<MatchFilter> matchFilters = new ArrayList<MatchFilter>(dimensions.length);
        for (Dimension dimension : dimensions) {
            FilterInfo filter = dimension.getFilter();
            if (filter != null && filter.isMatchFilter()) {
                matchFilters.add(FilterBuilder.buildMatchFilter(filter));
            }
        }
        return matchFilters;
    }
}
