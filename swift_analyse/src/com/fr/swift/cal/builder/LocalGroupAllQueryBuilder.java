package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.result.group.GroupResultQuery;
import com.fr.swift.cal.segment.group.GroupAllSegmentQuery;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupAllQueryBuilder extends AbstractLocalGroupQueryBuilder {


    @Override
    public Query<GroupByResultSet> buildLocalQuery(GroupQueryInfo info) {
        List<Query<GroupByResultSet>> queries = new ArrayList<Query<GroupByResultSet>>();
        for (TableGroupQueryInfo groupQueryInfo : info.getTableGroups()) {
            List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(groupQueryInfo.getTable());
            for (Segment segment : segments) {
                List<Column> dimensionSegments = getDimensionSegments(segment, groupQueryInfo.getDimensions());
                List<Column> metricSegments = getMetricSegments(segment, groupQueryInfo.getMetrics());
                List<Aggregator> aggregators = getAggregators(groupQueryInfo.getMetrics());
                queries.add(new GroupAllSegmentQuery(dimensionSegments, metricSegments, aggregators, FilterBuilder.buildDetailFilter(segment, info.getFilterInfo())));
            }
        }
        return new GroupResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()));
    }

    @Override
    public Query<GroupByResultSet> buildResultQuery(List<Query<GroupByResultSet>> queries, GroupQueryInfo info) {
        return new GroupResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()), getIndexSorts(info.getDimensions()), getDimensionMatchFilters(info.getDimensions()));
    }

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
        List<MatchFilter> matchFilters = new ArrayList<MatchFilter>();
        for (Dimension dimension : dimensions) {
            FilterInfo filter = dimension.getFilter();
            if (filter != null && filter.isMatchFilter()) {
                matchFilters.add(dimension.getIndex(), FilterBuilder.buildMatchFilter(filter));
            }
        }
        return matchFilters;
    }
}
