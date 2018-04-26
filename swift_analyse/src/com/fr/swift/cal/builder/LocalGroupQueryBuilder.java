package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.result.NodeResultSet;

import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public interface LocalGroupQueryBuilder {
    LocalGroupQueryBuilder SINGLE_URI = new LocalGroupSingleURIQueryBuilder();

    LocalGroupQueryBuilder PAGING = new LocalGroupPagingQueryBuilder();

    LocalGroupQueryBuilder ALL = new LocalGroupAllQueryBuilder();

    /**
     * 构建本地的结果查询Query，处理对本地节点多个SegmentQuery的合并
     *
     * @param info 查询信息
     * @return
     */
    ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info);

    /**
     * 构建多个ResultQuery查询Query，处理多个ResultQuery的合并
     * 每个ResultQuery对应一个swift计算节点上的合并结果(多个SegmentQuery的结果)
     *
     * @param queries 一组结果Query
     * @param info 查询信息
     * @return
     */
    ResultQuery<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info);

    /**
     * 处理查询计算的最后一步计算。在解析查询请求的时候，根据数据在swift计算节点上的分布来确认哪个节点执行这个query
     *
     * @param query 根据查询数据的分布，可能是单节点的ResultQuery，也可能是多节点合并的ResultQuery
     * @param info  查询信息
     * @return
     */
    Query<NodeResultSet> buildTargetCalQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info);
}
