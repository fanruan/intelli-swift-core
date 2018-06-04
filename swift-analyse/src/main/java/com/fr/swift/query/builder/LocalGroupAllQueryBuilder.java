package com.fr.swift.query.builder;

import com.fr.swift.compare.Comparators;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.Query;
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
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.post.HavingFilterQuery;
import com.fr.swift.query.post.PostQuery;
import com.fr.swift.query.post.PostQueryType;
import com.fr.swift.query.post.PrepareMetaDataQuery;
import com.fr.swift.query.post.ResultCalQuery;
import com.fr.swift.query.post.RowSortQuery;
import com.fr.swift.query.post.TreeAggregationQuery;
import com.fr.swift.query.post.TreeFilterQuery;
import com.fr.swift.query.post.TreeSortQuery;
import com.fr.swift.query.post.UpdateNodeDataQuery;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.query.result.group.GroupResultQuery;
import com.fr.swift.query.segment.group.GroupAllSegmentQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupAllQueryBuilder extends AbstractLocalGroupQueryBuilder {

    // TODO: 2018/5/31 结果的配置计算中值的读写还是依赖之前解析的数组的index，外部调用查询写得时候比较麻烦。查询属性写字段名，做一层解析吧
    @Override
    public Query<NodeResultSet> buildPostQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info) {
        PostQuery<NodeResultSet> tmpQuery = new UpdateNodeDataQuery(query);
        List<PostQueryInfo> postQueryInfoList = info.getPostQueryInfoList();
        for (PostQueryInfo postQueryInfo : postQueryInfoList) {
            PostQueryType type = postQueryInfo.getType();
            switch (type) {
                case CAL_FIELD:
                    tmpQuery = new ResultCalQuery(tmpQuery, ((CalculatedFieldQueryInfo) postQueryInfo).getCalInfoList());
                    break;
                case HAVING_FILTER:
                    tmpQuery = new HavingFilterQuery(tmpQuery, null);
                    break;
                case TREE_FILTER:
                    tmpQuery = new TreeFilterQuery(tmpQuery, null);
                    break;
                case TREE_AGGREGATION:
                    tmpQuery = new TreeAggregationQuery(tmpQuery, null);
                    break;
                case TREE_SORT:
                    tmpQuery = new TreeSortQuery(tmpQuery, null);
                    break;
                case ROW_SORT:
                    tmpQuery = new RowSortQuery(tmpQuery, null);
                    break;
            }
        }
        //最后一层query的结果要包含SwiftMetaData
        return new PrepareMetaDataQuery(tmpQuery, info);
    }

    @Override
    public ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        List<Metric> metrics = info.getMetrics();
        List<Dimension> dimensions = info.getDimensions();
        List<Segment> segments = SwiftContext.getInstance().getBean(LocalSegmentProvider.class).getSegment(info.getTable());
        for (Segment segment : segments) {
            List<Column> dimensionColumns = getDimensionSegments(segment, dimensions);
            List<Column> metricColumns = getMetricSegments(segment, metrics);
            List<Aggregator> aggregators = getFilterAggregators(metrics, segment);
            List<Sort> rowIndexSorts = getSegmentIndexSorts(dimensions);
            DetailFilter rowDetailFilter = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
            GroupByInfo rowGroupByInfo = new GroupByInfoImpl(dimensionColumns, rowDetailFilter, rowIndexSorts, new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<RowIndexKey<String[]>>()));
            // TODO: 2018/5/30 AggregatorValueContainer用map还是数组的取舍
            // 数组读写存储效率好但是解析麻烦，map占用空间大一点计算解析方便
            MetricInfo metricInfo = new MetricInfoImpl(metricColumns, aggregators, metrics.size());
            // TODO: 2018/5/31 segmentQuery也能做部分过滤，比如有全局字段的情况下的前N个过滤
            queries.add(new GroupAllSegmentQuery(rowGroupByInfo, metricInfo));
        }
        return new GroupResultQuery(queries, getAggregators(metrics), getComparatorsForMerge(dimensions));
    }

    private List<Aggregator> getFilterAggregators(List<Metric> metrics, Segment segment) {
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

    private static List<Comparator<Integer>> getComparatorsForMerge(List<Dimension> dimensions) {
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
