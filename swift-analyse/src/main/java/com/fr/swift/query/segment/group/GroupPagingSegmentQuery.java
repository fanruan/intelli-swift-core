package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingSegmentQuery extends AbstractGroupSegmentQuery {

    // TODO: 2018/7/25 这个改为fetchSize
    private static final int PAGE_SIZE = 200;

    public GroupPagingSegmentQuery(GroupByInfo pageGroupByInfo, MetricInfo metricInfo) {
        super(pageGroupByInfo, metricInfo);
    }

    @Override
    public NodeResultSet getQueryResult() {
        final Iterator<NodeMergeResultSet<GroupNode>> iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo, PAGE_SIZE);
        return new NodeMergeResultSet() {

            private List<Map<Integer, Object>> dictionary;

            @Override
            public SwiftMetaData getMetaData() {
                return null;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Row getNextRow() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public SwiftNode getNode() {
                NodeMergeResultSet resultSet = iterator.next();
                dictionary = resultSet.getRowGlobalDictionaries();
                return resultSet.getNode();
            }

            @Override
            public boolean hasNextPage() {
                return iterator.hasNext();
            }

            @Override
            public List<Map<Integer, Object>> getRowGlobalDictionaries() {
                return dictionary;
            }
        };
    }
}
