package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.segment.AbstractSegmentQuery;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/12/18.
 */
public abstract class AbstractGroupSegmentQuery extends AbstractSegmentQuery<QueryResultSet> {

    protected GroupByInfo groupByInfo;
    protected MetricInfo metricInfo;

    public AbstractGroupSegmentQuery(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        this.groupByInfo = groupByInfo;
        this.metricInfo = metricInfo;
    }

}
