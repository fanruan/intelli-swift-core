package com.fr.swift.query.builder;

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
import com.fr.swift.query.post.PostQuery;
import com.fr.swift.query.post.RowSortQuery;
import com.fr.swift.query.post.TreeAggregationQuery;
import com.fr.swift.query.post.TreeFilterQuery;
import com.fr.swift.query.post.TreeSortQuery;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.List;

/**
 * Created by Lyon on 2018/6/6.
 */
class PostQueryBuilder {

    static PostQuery<QueryResultSet> buildQuery(PostQuery<QueryResultSet> tmpQuery, List<PostQueryInfo> postQueryInfoList) {
        for (PostQueryInfo postQueryInfo : postQueryInfoList) {
            PostQueryType type = postQueryInfo.getType();
            switch (type) {
                case CAL_FIELD:
                    tmpQuery = new FieldCalQuery(tmpQuery, ((CalculatedFieldQueryInfo) postQueryInfo).getCalInfo());
                    break;
                case HAVING_FILTER:
                    tmpQuery = new HavingFilterQuery(tmpQuery, ((HavingFilterQueryInfo) postQueryInfo).getMatchFilterList());
                    break;
                case TREE_FILTER:
                    tmpQuery = new TreeFilterQuery(tmpQuery, ((TreeFilterQueryInfo) postQueryInfo).getMatchFilterList());
                    break;
                case TREE_AGGREGATION:
                    tmpQuery = new TreeAggregationQuery(tmpQuery, ((TreeAggregationQueryInfo) postQueryInfo).getAggregators());
                    break;
                case TREE_SORT:
                    tmpQuery = new TreeSortQuery(tmpQuery, ((TreeSortQueryInfo) postQueryInfo).getSortList());
                    break;
                case ROW_SORT:
                    tmpQuery = new RowSortQuery(tmpQuery, ((RowSortQueryInfo) postQueryInfo).getSortList());
                    break;
                case FUNNEL_MEDIAN:
                    tmpQuery = new FunnelPostQuery(tmpQuery, ((FunnelPostQueryInfo) postQueryInfo).getQueryBean());
            }
        }
        return tmpQuery;
    }
}
