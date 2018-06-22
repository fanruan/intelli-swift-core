package com.fr.swift.query.builder;

import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.RemoteQueryInfoImpl;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.remote.GroupLocalAllQuery;
import com.fr.swift.query.remote.GroupLocalPartQuery;
import com.fr.swift.query.remote.RemoteQueryImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
class GroupQueryBuilder {

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param info
     * @return
     */
    static Query<NodeResultSet> buildQuery(GroupQueryInfo info) {
        SourceKey table = info.getTable();
        // TODO 这边先直接写成History
        List<SegmentDestination> uris = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            return buildQuery(uris, info, LocalGroupQueryBuilder.PAGING);
        } else {
            return buildQuery(uris, info, LocalGroupQueryBuilder.ALL);
        }
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的部分分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<NodeResultSet> buildLocalPartQuery(GroupQueryInfo info) {
        Query<NodeResultSet> query;
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            query = LocalGroupQueryBuilder.PAGING.buildLocalQuery(info);
        } else {
            query = LocalGroupQueryBuilder.ALL.buildLocalQuery(info);
        }
        return new GroupLocalPartQuery(info.getQueryId(), query);
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的全部分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<NodeResultSet> buildLocalAllQuery(GroupQueryInfo info) {
        Query<NodeResultSet> query;
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            query = LocalGroupQueryBuilder.PAGING.buildPostQuery(LocalGroupQueryBuilder.PAGING.buildLocalQuery(info), info);
        } else {
            query = LocalGroupQueryBuilder.ALL.buildPostQuery(LocalGroupQueryBuilder.ALL.buildLocalQuery(info), info);
        }
        return new GroupLocalAllQuery(info.getQueryId(), query);
    }

    /**
     * 如果uris.size() == 0，那么只有一个子节点负责查询，同时这个子节点应该负责计算计算指标
     * 之前根据处理查询数据的方式，query分为两类：SegmentQuery和ResultQuery
     * 查询的计算主要分三类：明细聚合(SegmentQuery)、聚合结果合并、依赖结果的计算(ResultQuery同时负责后两者)
     * query的嵌套方式为：ResultQuery(ResultQuery(SegmentQuery, ...), ...)
     * 为了确认结果计算分给哪层的ResultQuery做，以及保证外层ResultQuery不会重复做结果计算
     * 在解析QueryInfo的时候将Query分三类，分别对应三个计算类型
     * SegmentQuery、ResultQuery、PostQuery
     * 1、外层节点负责结果计算PostQuery(ResultQuery(ResultQuery(SegmentQuery, ...), ...))，这个PostQuery在合并多个节点结果的机器上
     * 2、子节点负责结果计算PostQuery(ResultQuery(SegmentQuery, ...))，这个PostQuery在拥有全部数据的节点上
     *
     * 这边buildQuery的转发至多经过两个节点，RemoteQuery调用buildQuery的时候不会有RemoteQuery了
     * 这就要保证当前analysisService节点拥有所有的SegmentLocation信息
     *
     * @param uris    数据在节点上的分布情况
     * @param info    查询信息
     * @param builder 本地查询解析
     * @return 获取最后查询结果的Query
     */
    private static Query<NodeResultSet> buildQuery(List<SegmentDestination> uris, GroupQueryInfo info, LocalGroupQueryBuilder builder) {
        if (DetailQueryBuilder.isAllLocal(uris)) {
            return builder.buildPostQuery(builder.buildLocalQuery(info), info);
        }
        // TODO: 2018/6/22 全部segment在一个远程节点上
//        if (uris.size() == 1) {
//            // 如果数据只分布在一个节点上面，那么在该节点上面完成最后一步计算指标计算
//            if (!uris.get(0).isRemote()) {
//                return builder.buildPostQuery(builder.buildLocalQuery(info), info);
//            } else {
//                // 丢给远程节点
//                QueryInfo<NodeResultSet> queryInfo = new RemoteQueryInfoImpl<NodeResultSet>(QueryType.LOCAL_GROUP_ALL, info);
//                return new RemoteQueryImpl<NodeResultSet>(queryInfo, uris.get(0));
//            }
//        }
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        for (SegmentDestination uri : uris) {
            if (!uri.isRemote()) {
                queries.add(builder.buildLocalQuery(info));
            } else {
                QueryInfo<NodeResultSet> queryInfo = new RemoteQueryInfoImpl<NodeResultSet>(QueryType.LOCAL_GROUP_PART, info);
                // TODO: 2018/6/6 这边有个问题，远程节点有多个segment怎么办呢？RemoteQuery只对应一个segment的uri的话，远程节点就不能做合并了
                queries.add(new RemoteQueryImpl<NodeResultSet>(queryInfo, uri));
            }
        }
        // 多个节点的ResultQuery合并之后在处理List<PostQueryInfo>
        return builder.buildPostQuery(builder.buildResultQuery(queries, info), info);
    }
}
