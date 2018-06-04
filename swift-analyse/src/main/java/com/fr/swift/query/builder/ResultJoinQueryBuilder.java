package com.fr.swift.query.builder;

import com.fr.swift.query.Query;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryType;
import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.post.ResultJoinQuery;
import com.fr.swift.result.NodeResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class ResultJoinQueryBuilder {

    static Query<NodeResultSet> buildQuery(ResultJoinQueryInfo info) throws SQLException {
        List<QueryInfo> infoList = info.getQueryInfoList();
        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
        for (QueryInfo queryInfo : infoList) {
            // 限制都是group类型的查询，各个group查询的维度的排序都是索引默认的PINYIN_ASC
            assert queryInfo.getType() == QueryType.GROUP;
            queries.add(QueryBuilder.buildQuery(queryInfo));
        }
        return new ResultJoinQuery(queries, info.getJoinedDimensions());
    }
}
