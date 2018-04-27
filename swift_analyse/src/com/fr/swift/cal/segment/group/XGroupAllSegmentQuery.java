package com.fr.swift.cal.segment.group;

import com.fr.swift.query.group.by.XGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.NodeResultSetImpl;
import com.fr.swift.result.XGroupByResultSet;
import com.fr.swift.result.node.xnode.XLeftNode;
import com.fr.swift.result.node.xnode.XLeftNodeFactory;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XGroupAllSegmentQuery extends GroupAllSegmentQuery {

    private GroupByInfo colGroupByInfo;

    public XGroupAllSegmentQuery(GroupByInfo rowGroupByInfo, GroupByInfo colGroupByInfo, MetricInfo metricInfo) {
        super(rowGroupByInfo, metricInfo);
        this.colGroupByInfo = colGroupByInfo;
    }

    @Override
    public NodeResultSet getQueryResult() {
        XGroupByResultSet resultSet = XGroupByUtils.query(groupByInfo, colGroupByInfo, metricInfo, -1, -1);
        XLeftNode node = XLeftNodeFactory.createXLeftNode(resultSet, metricInfo.getTargetLength());
        return new NodeResultSetImpl(node);
    }
}
