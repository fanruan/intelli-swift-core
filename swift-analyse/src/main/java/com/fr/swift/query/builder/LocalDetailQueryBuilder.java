package com.fr.swift.query.builder;

import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/12/14.
 */
public interface LocalDetailQueryBuilder {
    LocalDetailQueryBuilder NORMAL = new LocalDetailNormalQueryBuilder();

    LocalDetailQueryBuilder GROUP = new LocalDetailGroupQueryBuilder();

    /**
     * 创建本地的查询,不处理targets
     * @param info
     * @return
     */
    <T extends QueryResultSet> Query<T> buildLocalQuery(DetailQueryInfo info);
}
