package com.fr.swift.query.post;

import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.post.utils.ResultJoinUtils;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对应关联数据库中的几个子表通过关联字段组成一张大表，然后依据关联字段做groupBy的操作
 * 在关联字段对应的分组数不是很多的时候，不通过关联来算大表，直接在子表上做groupBy，然后再对几个子表的聚合结果做合并
 * 感觉这个处理逻辑不暴露给用户比较好，用户查询的时候就按照关联的模式进行查询（子表关联 > 大表 > groupBy)
 * <p>
 * Created by Lyon on 2018/5/31.
 */
public class ResultJoinQuery implements PostQuery<QueryResultSet> {

    private List<Query<QueryResultSet>> queries;
    private List<Dimension> dimensions;

    public ResultJoinQuery(List<Query<QueryResultSet>> queries, List<Dimension> dimensions) {
        this.queries = queries;
        this.dimensions = dimensions;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        List<NodeResultSet> resultSets = new ArrayList<NodeResultSet>();
        for (Query<QueryResultSet> query : queries) {
            resultSets.add((NodeResultSet) query.getQueryResult());
        }
        // TODO: 2018/11/27
        return (QueryResultSet) ResultJoinUtils.join(resultSets, dimensions);
    }
}
