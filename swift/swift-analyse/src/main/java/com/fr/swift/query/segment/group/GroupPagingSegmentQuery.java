package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.BaseNodeMergeResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingSegmentQuery extends AbstractGroupSegmentQuery {

    public GroupPagingSegmentQuery(GroupByInfo pageGroupByInfo, MetricInfo metricInfo) {
        super(pageGroupByInfo, metricInfo);
    }

    @Override
    public QueryResultSet getQueryResult() {
        final Iterator<NodeMergeResultSet<GroupNode>> iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
        return new BaseNodeMergeResultSet<GroupNode>(groupByInfo.getFetchSize()) {
            @Override
            public Pair<GroupNode, List<Map<Integer, Object>>> getPage() {
                NodeMergeResultSet<GroupNode> resultSet = iterator.next();
                return resultSet.getPage();
            }

            @Override
            public boolean hasNextPage() {
                return iterator.hasNext();
            }
        };
    }
}
