package com.fr.swift.cal.segment.group;

import com.fr.swift.query.group.by2.node.XNodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.NodeResultSet;

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
        return XNodeGroupByUtils.groupBy(colGroupByInfo, colGroupByInfo, metricInfo);
    }
}
