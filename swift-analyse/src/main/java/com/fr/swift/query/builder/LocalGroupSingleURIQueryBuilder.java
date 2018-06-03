package com.fr.swift.query.builder;

import com.fr.swift.query.Query;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.post.group.GroupPostQuery;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.query.result.group.GroupPagingResultQuery;
import com.fr.swift.result.NodeResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/18.
 * todo
 * 待实现，单机优化
 */
public class LocalGroupSingleURIQueryBuilder extends AbstractLocalGroupQueryBuilder{

    @Override
    public Query<NodeResultSet> buildPostQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info) {
        return new GroupPostQuery(query, info);
    }

    @Override
    public ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
//        return new GroupSingleURIQuery();
        return null;
    }

    @Override
    public ResultQuery<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info) {
        return new GroupPagingResultQuery(queries, new ArrayList<Aggregator>(), new ArrayList<GroupTarget>());
    }
}
