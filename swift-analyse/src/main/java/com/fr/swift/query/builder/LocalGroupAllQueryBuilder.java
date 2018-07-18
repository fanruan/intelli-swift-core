package com.fr.swift.query.builder;

import com.fr.swift.compare.Comparators;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.MetricInfoImpl;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.post.PostQuery;
import com.fr.swift.query.post.PrepareMetaDataQuery;
import com.fr.swift.query.post.UpdateNodeDataQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.query.result.group.GroupResultQuery;
import com.fr.swift.query.segment.group.GroupAllSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupAllQueryBuilder extends AbstractLocalGroupQueryBuilder {

    // TODO: 2018/5/31 结果的配置计算中值的读写还是依赖之前解析的数组的index，外部调用查询写得时候比较麻烦。查询属性写字段名，做一层解析吧
    // TODO: 2018/6/5 关于分页计算和结果集分页的问题，检查List<PostQueryInfo>有没有需要全部计算的条件，
    // 如果有结果过滤、排序、组内计算和topN等则全部计算，否则进行分页计算。全部计算的情况下，内部节点之间传递全部结果集，最后一个
    // 节点提供结果集分页的实现。分页计算情况下，内部节点之间传递部分结果集，结合最后一个节点的分页实现按需计算。
    @Override
    public Query<NodeResultSet> buildPostQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info) {
        PostQuery<NodeResultSet> tmpQuery = new UpdateNodeDataQuery(query);
        List<PostQueryInfo> postQueryInfoList = info.getPostQueryInfoList();
        tmpQuery = PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList);
        // 最后一层query的结果要包含SwiftMetaData
        return new PrepareMetaDataQuery(tmpQuery, info);
    }

    @Override
    public ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        List<Metric> metrics = info.getMetrics();
        List<Dimension> dimensions = info.getDimensions();
        List<Segment> segments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(info.getTable());
        List<Segment> targetSegments = LocalDetailNormalQueryBuilder.getSegmentsByURIList(info.getQuerySegment(), segments);
        if (targetSegments.isEmpty()) {
            targetSegments = segments;
        }
        targetSegments = Collections.unmodifiableList(targetSegments);
        for (Segment segment : targetSegments) {
            List<Column> dimensionColumns = getDimensionSegments(segment, dimensions);
            List<Column> metricColumns = getMetricSegments(segment, metrics);
            List<Aggregator> aggregators = getFilterAggregators(metrics, segment);
            List<Sort> rowIndexSorts = getSegmentIndexSorts(dimensions);
            DetailFilter rowDetailFilter = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
            GroupByInfo rowGroupByInfo = new GroupByInfoImpl(dimensionColumns, rowDetailFilter, rowIndexSorts, new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<RowIndexKey<String[]>>()), null);
            // TODO: 2018/5/30 AggregatorValueContainer用map还是数组的取舍
            // 数组读写存储效率好但是解析麻烦，map占用空间大一点计算解析方便
            MetricInfo metricInfo = new MetricInfoImpl(metricColumns, aggregators, metrics.size());
            // TODO: 2018/5/31 segmentQuery也能做部分过滤，比如有全局字段的情况下的前N个过滤
            queries.add(new GroupAllSegmentQuery(rowGroupByInfo, metricInfo));
        }
        return new GroupResultQuery(queries, getAggregators(metrics), getComparatorsForMerge(dimensions));
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

    @Override
    public ResultQuery<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info) {
        // TODO: 2018/5/30 这个对ResultQuery进行合并的ResultQuery只能明细聚合结果的合并？
        return new GroupResultQuery(queries, getAggregators(info.getMetrics()),
                getComparatorsForMerge(info.getDimensions()));
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

    static List<Comparator<Integer>> getComparatorsForMerge(List<Dimension> dimensions) {
        List<Comparator<Integer>> comparators = new ArrayList<Comparator<Integer>>(dimensions.size());
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() == dimension.getIndex()) {
                if (sort.getSortType() == SortType.ASC) {
                    comparators.add(Comparators.<Integer>asc());
                } else {
                    comparators.add(Comparators.<Integer>desc());
                }
            }
            comparators.add(Comparators.<Integer>asc());
        }
        return comparators;
    }
}
