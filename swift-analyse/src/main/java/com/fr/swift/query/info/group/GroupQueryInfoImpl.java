package com.fr.swift.query.info.group;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.AbstractQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/11
 */
public class GroupQueryInfoImpl extends AbstractQueryInfo<NodeMergeQRS> implements GroupQueryInfo<NodeMergeQRS> {

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
