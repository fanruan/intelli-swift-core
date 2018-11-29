package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.Iterator;

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
//        return new NodeMergeResultSet() {
//
//            @Override
//            public SwiftMetaData getMetaData() {
//                return null;
//            }
//
//            @Override
//            public boolean hasNext() {
//                return false;
//            }
//
//            @Override
//            public Row getNextRow() {
//                return null;
//            }
//
//            @Override
//            public void close() {
//
//            }
//
//            @Override
//            public int getFetchSize() {
//                return groupByInfo.getFetchSize();
//            }
//
//            @Override
//            public Pair<GroupNode, List<Map<Integer, Object>>> getPage() {
//                NodeMergeResultSet resultSet = iterator.next();
//                return resultSet.getPage();
//            }
//
//            @Override
//            public boolean hasNextPage() {
//                return iterator.hasNext();
//            }
//        };
        // TODO: 2018/11/27
        return null;
    }
}
