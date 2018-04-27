package com.fr.swift.cal.segment.group;

import com.fr.swift.query.group.by.GroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.NodeResultSetImpl;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.node.GroupNodeFactory;

import java.util.Arrays;

/**
 * Created by pony on 2017/12/18.
 */
public class GroupAllSegmentQuery extends AbstractGroupSegmentQuery{

    public GroupAllSegmentQuery(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        super(groupByInfo, metricInfo);
    }

    @Override
    public NodeResultSet getQueryResult() {
        int[] cursor = new int[groupByInfo.getDimensions().size()];
        Arrays.fill(cursor, 0);
        GroupByResultSet resultSet = GroupByUtils.query(groupByInfo, metricInfo, -1);
        SwiftNode node = GroupNodeFactory.createNode(resultSet, metricInfo.getTargetLength());
        return new NodeResultSetImpl<SwiftNode>(node);
    }
}
