package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by.GroupByUtils;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.group.info.PageGroupByInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.NodeResultSetImpl;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.node.GroupNodeFactory;
import com.fr.swift.result.row.GroupByResultSet;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingSegmentQuery extends AbstractGroupSegmentQuery {

    public GroupPagingSegmentQuery(PageGroupByInfo pageGroupByInfo, MetricInfo metricInfo) {
        super(pageGroupByInfo, metricInfo);
    }

    @Override
    public NodeResultSet getQueryResult() {
        GroupByResultSet result = GroupByUtils.query(groupByInfo, metricInfo, -1);
        SwiftNode node = GroupNodeFactory.createNode(result, metricInfo.getTargetLength());
        return new NodeResultSetImpl(node);
    }
}
