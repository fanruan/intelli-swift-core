package com.fr.swift.cloud.query.segment.group;

import com.fr.swift.cloud.query.group.by2.node.GroupPage;
import com.fr.swift.cloud.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.cloud.query.group.info.GroupByInfo;
import com.fr.swift.cloud.query.group.info.MetricInfo;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.Iterator;

/**
 * @author pony
 * @date 2017/12/18
 */
public class GroupSegmentQuery implements Query<QueryResultSet<GroupPage>> {
    private GroupByInfo groupByInfo;

    private MetricInfo metricInfo;

    public GroupSegmentQuery(GroupByInfo groupByInfo, MetricInfo metricInfo) {
        this.groupByInfo = groupByInfo;
        this.metricInfo = metricInfo;
    }

    @Override
    public QueryResultSet<GroupPage> getQueryResult() {
        Iterator<GroupPage> iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
        return new IteratorAdapter<GroupPage>(iterator, groupByInfo.getFetchSize());
    }

    private static class IteratorAdapter<P> implements QueryResultSet<P> {
        Iterator<P> pageItr;
        int fetchSize;

        IteratorAdapter(Iterator<P> pageItr, int fetchSize) {
            this.pageItr = pageItr;
            this.fetchSize = fetchSize;
        }

        @Override
        public int getFetchSize() {
            return fetchSize;
        }

        @Override
        public SwiftResultSet convert(SwiftMetaData metaData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public P getPage() {
            return pageItr.next();
        }

        @Override
        public boolean hasNextPage() {
            return pageItr.hasNext();
        }

        @Override
        public void close() {
            // TODO: 2019-07-01 anchore 要将close传递到groupby内部，真正的释放资源
//            throw new UnsupportedOperationException();
        }
    }
}
