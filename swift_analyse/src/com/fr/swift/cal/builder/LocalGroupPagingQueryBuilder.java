package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.result.group.GroupPagingResultQuery;
import com.fr.swift.cal.segment.group.GroupPagingSegmentQuery;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.result.RowResultCollector;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class LocalGroupPagingQueryBuilder extends AbstractLocalGroupQueryBuilder {
    @Override
    public Query<RowResultCollector> buildLocalQuery(GroupQueryInfo info) {
        List<Query<RowResultCollector>> queries = new ArrayList<Query<RowResultCollector>>();
        for (TableGroupQueryInfo groupQueryInfo : info.getTableGroups()) {
            List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(groupQueryInfo.getTable());
            for (Segment segment : segments) {
                List<Column> dimensionSegments = getDimensionSegments(segment, groupQueryInfo.getDimensions());
                List<Column> metricSegments = getMetricSegments(segment, groupQueryInfo.getMetrics());
                List<Aggregator> aggregators = getAggregators(groupQueryInfo.getMetrics());
                queries.add(new GroupPagingSegmentQuery(dimensionSegments, metricSegments, aggregators, FilterBuilder.buildDetailFilter(segment, info.getFilterInfo()), info.getExpander()));
            }
        }
        return new GroupPagingResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()));
    }

    @Override
    public Query<RowResultCollector> buildResultQuery(List<Query<RowResultCollector>> queries, GroupQueryInfo info) {
        return new GroupPagingResultQuery(queries, getAggregators(info.getMetrics()), getTargets(info.getTargets()));
    }
}
