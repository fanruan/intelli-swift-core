package com.fr.swift.query.builder;

import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.post.PrepareMetaDataQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.remote.RemoteQueryImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pony on 2017/12/14.
 */
class GroupQueryBuilder {

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param bean
     * @return
     */
    static Query<NodeResultSet> buildQuery(GroupQueryInfoBean bean) throws Exception {
        GroupQueryInfo info = (GroupQueryInfo) QueryInfoParser.parse(bean);
        SourceKey table = info.getTable();
        List<SegmentDestination> uris = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        Query<NodeResultSet> query;
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            query = buildQuery(uris, info, bean, LocalGroupQueryBuilder.PAGING);
        } else {
            query = buildQuery(uris, info, bean, LocalGroupQueryBuilder.ALL);
        }
        return new PrepareMetaDataQuery<NodeResultSet>(query, bean);
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的部分分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<NodeResultSet> buildLocalPartQuery(GroupQueryInfo info) {
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            return LocalGroupQueryBuilder.PAGING.buildLocalQuery(info);
        } else {
            return LocalGroupQueryBuilder.ALL.buildLocalQuery(info);
        }
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的全部分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<NodeResultSet> buildLocalAllQuery(GroupQueryInfo info) {
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            return LocalGroupQueryBuilder.PAGING.buildPostQuery(LocalGroupQueryBuilder.PAGING.buildLocalQuery(info), info);
        } else {
            return LocalGroupQueryBuilder.ALL.buildPostQuery(LocalGroupQueryBuilder.ALL.buildLocalQuery(info), info);
        }
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
    private static Query<NodeResultSet> buildQuery(List<SegmentDestination> uris, GroupQueryInfo info,
                                                   GroupQueryInfoBean queryBean, LocalGroupQueryBuilder builder) throws JsonProcessingException {
        if (DetailQueryBuilder.isAllLocal(uris)) {
            return builder.buildPostQuery(builder.buildLocalQuery(info), info);
        }
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        Set<String> localURIs = DetailQueryBuilder.getLocalSegments(uris);
        if (!localURIs.isEmpty()) {
            info.setQuerySegment(localURIs);
            queries.add(builder.buildLocalQuery(info));
        }
        Map<String, List<SegmentDestination>> map = DetailQueryBuilder.groupSegmentInfoByClusterId(uris);
        if (localURIs.isEmpty() && map.size() == 1) {
            // 全部数据在一个远程节点
            queryBean.setQueryType(QueryType.LOCAL_GROUP_ALL);
            Map.Entry<String, List<SegmentDestination>> entry = map.entrySet().iterator().next();
            SegmentDestination destination = entry.getValue().get(0);
            queryBean.setQueryDestination(destination);
            queryBean.setQuerySegments(DetailQueryBuilder.getQuerySegments(entry.getValue()));
            String jsonString = QueryInfoBeanFactory.queryBean2String(queryBean);
            return new RemoteQueryImpl<NodeResultSet>(jsonString, destination);
        }

        for (Map.Entry<String, List<SegmentDestination>> entry : map.entrySet()) {
            queryBean.setQueryType(QueryType.LOCAL_GROUP_PART);
            SegmentDestination destination = entry.getValue().get(0);
            queryBean.setQueryDestination(destination);
            queryBean.setQuerySegments(DetailQueryBuilder.getQuerySegments(entry.getValue()));
            String jsonString = QueryInfoBeanFactory.queryBean2String(queryBean);
            queries.add(new RemoteQueryImpl<NodeResultSet>(jsonString, destination));
        }
        // 多个节点的ResultQuery合并之后在处理List<PostQueryInfo>
        return builder.buildPostQuery(builder.buildResultQuery(queries, info), info);
    }
}
