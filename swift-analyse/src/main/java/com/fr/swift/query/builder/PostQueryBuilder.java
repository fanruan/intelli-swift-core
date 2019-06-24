package com.fr.swift.query.builder;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import com.fr.swift.query.info.group.post.FunnelPostQueryInfo;
import com.fr.swift.query.info.group.post.HavingFilterQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.info.group.post.RowSortQueryInfo;
import com.fr.swift.query.info.group.post.TreeAggregationQueryInfo;
import com.fr.swift.query.info.group.post.TreeFilterQueryInfo;
import com.fr.swift.query.info.group.post.TreeSortQueryInfo;
import com.fr.swift.query.post.FieldCalQuery;
import com.fr.swift.query.post.FunnelPostQuery;
import com.fr.swift.query.post.HavingFilterQuery;
import com.fr.swift.query.post.RowSortQuery;
import com.fr.swift.query.post.TreeAggregationQuery;
import com.fr.swift.query.post.TreeFilterQuery;
import com.fr.swift.query.post.TreeSortQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.List;

/**
 * Created by Lyon on 2018/6/6.
 */
class PostQueryBuilder {

    static <Q extends QueryResultSet<?>> Query<Q> buildQuery(Query<Q> tmpQuery, List<PostQueryInfo> postQueryInfoList) {
        for (PostQueryInfo postQueryInfo : postQueryInfoList) {
            PostQueryType type = postQueryInfo.getType();
            switch (type) {
                case CAL_FIELD:
                    return (Query<Q>) new FieldCalQuery((Query<QueryResultSet<SwiftNode>>) tmpQuery, ((CalculatedFieldQueryInfo) postQueryInfo).getCalInfo());
                case HAVING_FILTER:
                    return (Query<Q>) new HavingFilterQuery((Query<QueryResultSet<SwiftNode>>) tmpQuery, ((HavingFilterQueryInfo) postQueryInfo).getMatchFilterList());
                case TREE_FILTER:
                    return (Query<Q>) new TreeFilterQuery((Query<QueryResultSet<SwiftNode>>) tmpQuery, ((TreeFilterQueryInfo) postQueryInfo).getMatchFilterList());
                case TREE_AGGREGATION:
                    return (Query<Q>) new TreeAggregationQuery((Query<QueryResultSet<GroupPage>>) tmpQuery, ((TreeAggregationQueryInfo) postQueryInfo).getAggregators());
                case TREE_SORT:
                    return (Query<Q>) new TreeSortQuery((Query<QueryResultSet<SwiftNode>>) tmpQuery, ((TreeSortQueryInfo) postQueryInfo).getSortList());
                case ROW_SORT:
                    return (Query<Q>) new RowSortQuery((Query<QueryResultSet<SwiftNode>>) tmpQuery, ((RowSortQueryInfo) postQueryInfo).getSortList());
                case FUNNEL_MEDIAN:
                    return (Query<Q>) new FunnelPostQuery((Query<FunnelQueryResultSet>) tmpQuery, ((FunnelPostQueryInfo) postQueryInfo).getQueryBean());
                default:
            }
        }
        return tmpQuery;
    }
}
