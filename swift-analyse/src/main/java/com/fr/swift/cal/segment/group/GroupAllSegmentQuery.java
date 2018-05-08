package com.fr.swift.cal.segment.group;

import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.NodeResultSet;

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
        return NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
    }
}
