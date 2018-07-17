package com.fr.swift.query.builder;

import com.fr.swift.query.info.NestedQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.result.NodeResultSet;

/**
 * Created by Lyon on 2018/6/6.
 */
class NestedQueryBuilder {

    static Query<NodeResultSet> buildQuery(NestedQueryInfo<NodeResultSet> info) throws Exception {
        QueryInfo<NodeResultSet> queryInfo = info.getSubQueryInfo();
        Query<NodeResultSet> tmpQuery = QueryBuilder.buildQuery(queryInfo);
        // TODO: 2018/6/6 这边的嵌套查询只能简单地支持
        return null;
    }
}
