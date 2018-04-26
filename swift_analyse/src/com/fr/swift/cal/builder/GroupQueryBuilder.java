package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.remote.RemoteQueryImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/14.
 */
public class GroupQueryBuilder {

    protected static Query<NodeResultSet> buildQuery(GroupQueryInfo info) {
        SourceKey key = info.getTable();
        Set<URI> uris = SegmentLocationProvider.getInstance().getURI(key);
//        if (info.isPagingQuery()) {
        if (false) {
            return buildQuery(uris, info, LocalGroupQueryBuilder.PAGING);
        } else {
            return buildQuery(uris, info, LocalGroupQueryBuilder.ALL);
        }
    }

    /**
     * 如果uris.size() == 0，那么只有一个子节点负责查询，同时这个子节点应该负责计算计算指标
     * 当前根据处理查询数据的方式，query分为两类：SegmentQuery和ResultQuery
     * 查询的计算主要分三类：明细聚合(SegmentQuery)、聚合结果合并、依赖结果的计算(ResultQuery同时负责后两者)
     * query的嵌套方式为：ResultQuery(ResultQuery(SegmentQuery, ...), ...)
     * 为了确认结果计算分给哪层的ResultQuery做，以及保证外层ResultQuery不会重复做结果计算
     * 在解析QueryInfo的时候将Query分三类，分别对应三个计算类型
     * SegmentQuery、ResultQuery、ResultCalQuery
     * 1、外层节点负责结果计算ResultCalQuery(ResultQuery(SegmentQuery, ...), ...)
     * 2、子节点负责结果计算ResultQuery(ResultCalQuery(SegmentQuery, ...))
     *
     * @param uris    数据在节点上的分布情况
     * @param info    查询信息
     * @param builder 本地查询解析
     * @return 获取最后查询结果的Query
     */
    private static Query<NodeResultSet> buildQuery(Set<URI> uris, GroupQueryInfo info, LocalGroupQueryBuilder builder) {
        if (uris.size() == 1) {
            // 如果数据只分布在一个节点上面，那么在该节点上面完成最后一步计算指标计算
            return builder.buildTargetCalQuery(builder.buildLocalQuery(info), info);
        }
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        for (URI uri : uris){
            if (QueryBuilder.isLocalURI(uri)) {
                queries.add(builder.buildLocalQuery(info));
            } else {
                queries.add(new RemoteQueryImpl());
            }
        }
        // 多个节点的ResultQuery合并之后在处理计算指标
        return builder.buildTargetCalQuery(builder.buildResultQuery(queries, info), info);
    }
}
