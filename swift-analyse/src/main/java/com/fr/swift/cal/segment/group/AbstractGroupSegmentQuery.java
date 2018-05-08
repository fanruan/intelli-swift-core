package com.fr.swift.cal.segment.group;

import com.fr.swift.cal.segment.AbstractSegmentQuery;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.NodeResultSet;

/**
 * Created by pony on 2017/12/18.
 */
public abstract class AbstractGroupSegmentQuery extends AbstractSegmentQuery<NodeResultSet> {

    protected GroupByInfo groupByInfo;
    protected MetricInfo metricInfo;

    public AbstractGroupSegmentQuery(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        this.groupByInfo = groupByInfo;
        this.metricInfo = metricInfo;
    }

}
