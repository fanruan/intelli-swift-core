package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.BaseNodeMergeQRS;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRSImpl;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author pony
 * @date 2017/12/18
 */
public class GroupSegmentQuery implements Query<QueryResultSet<GroupPage>> {
    private GroupByInfo groupByInfo;

    private MetricInfo metricInfo;

    private final boolean paged;

    public GroupSegmentQuery(GroupByInfo groupByInfo, MetricInfo metricInfo, boolean paged) {
        this.groupByInfo = groupByInfo;
        this.metricInfo = metricInfo;
        this.paged = paged;
    }

    public static GroupSegmentQuery ofPaged(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        return new GroupSegmentQuery(groupByInfo, metricInfo, true);
    }

    public static GroupSegmentQuery ofAll(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        return new GroupSegmentQuery(groupByInfo, metricInfo, false);
    }

    @Override
    public QueryResultSet<GroupPage> getQueryResult() {
        final Iterator<QueryResultSet<GroupPage>> iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
        if (paged) {
            return new BaseNodeMergeQRS(groupByInfo.getFetchSize()) {
                @Override
                public GroupPage getPage() {
                    QueryResultSet<GroupPage> resultSet = iterator.next();
                    return resultSet.getPage();
                }

                @Override
                public boolean hasNextPage() {
                    return iterator.hasNext();
                }
            };
        } else {
            return (iterator.hasNext() ? iterator.next() : new NodeMergeQRSImpl(groupByInfo.getFetchSize(),
                    new GroupNode(-1, null), new ArrayList<Map<Integer, Object>>()));
        }
    }
}
