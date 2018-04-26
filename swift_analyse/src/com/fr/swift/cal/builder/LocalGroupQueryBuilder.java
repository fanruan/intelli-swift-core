package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
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
     * 构建本地的结果查询Query
     * 这个方法应该叫做buildLocalResultQuery比较合适
     *
     * @param info 查询信息
     * @return
     */
    Query<NodeResultSet> buildLocalQuery(GroupQueryInfo info);

    /**
     * 构建合并一组结果Query的查询Query
     *
     * @param queries 一组结果Query
     * @param info    查询信息
     * @return
     */
    Query<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info);
}
