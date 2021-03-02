package com.fr.swift.cloud.query.info.group;

import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.info.AbstractQueryInfo;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.metric.Metric;
import com.fr.swift.cloud.query.info.group.post.PostQueryInfo;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.source.SourceKey;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/11
 */
public class GroupQueryInfoImpl extends AbstractQueryInfo implements GroupQueryInfo {

    private List<Metric> metrics;
    private List<PostQueryInfo> postQueryInfoList;

    public GroupQueryInfoImpl(String queryId, int fetchSize, SourceKey table, FilterInfo filterInfo, List<Dimension> dimensions,
                              List<Metric> metrics, List<PostQueryInfo> postQueryInfoList) {
        super(queryId, fetchSize, table, filterInfo, dimensions);
        this.metrics = metrics;
        this.postQueryInfoList = postQueryInfoList;
    }

    @Override
    public QueryType getType() {
        return QueryType.GROUP;
    }

    @Override
    public List<Metric> getMetrics() {
        return metrics;
    }

    @Override
    public List<PostQueryInfo> getPostQueryInfoList() {
        return postQueryInfoList;
    }
}
