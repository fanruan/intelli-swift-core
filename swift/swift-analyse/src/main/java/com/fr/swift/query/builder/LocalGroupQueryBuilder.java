package com.fr.swift.query.builder;

import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/12/15.
 */
public interface LocalGroupQueryBuilder {

    LocalGroupQueryBuilder PAGING = new LocalGroupPagingQueryBuilder();

    LocalGroupQueryBuilder ALL = new LocalGroupAllQueryBuilder();

    /**
     * 构建本地的结果查询Query，处理对本地节点多个SegmentQuery的合并
     *
     * @param info 查询信息
     * @return
     */
    <T extends QueryResultSet> Query<T> buildLocalQuery(GroupQueryInfo info);
}
