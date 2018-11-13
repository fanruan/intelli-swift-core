package com.fr.swift.query.builder;

import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.DetailResultSet;

import java.util.List;

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
    Query<DetailResultSet> buildLocalQuery(DetailQueryInfo info);

    /**
     *
     * @param queries
     * @param info
     * @return
     */
    Query<DetailResultSet> buildResultQuery(List<Query<DetailResultSet>> queries, DetailQueryInfo info);
}
