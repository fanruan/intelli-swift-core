package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.structure.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/12/18.
 */
public class GroupAllSegmentQuery extends AbstractGroupSegmentQuery{

    public GroupAllSegmentQuery(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        super(groupByInfo, metricInfo);
    }

    @Override
    public NodeResultSet getQueryResult() {
        Iterator<NodeMergeResultSet<GroupNode>> iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
        NodeMergeResultSet<GroupNode> resultSet = iterator.next();
        // 返回全部结果集
        Pair<GroupNode, List<Map<Integer, Object>>> pair = resultSet.getPage();
        return new NodeMergeResultSetImpl(groupByInfo.getFetchSize(), pair.getKey(), pair.getValue());
    }
}
