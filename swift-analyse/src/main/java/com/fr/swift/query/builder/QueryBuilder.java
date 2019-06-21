package com.fr.swift.query.builder;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.post.UpdateNodeDataQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by pony on 2017/12/12.
 */
public final class QueryBuilder {

    /**
     * 本地解析查询字符串
     *
     * @param queryString
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends QueryResultSet<?>> Query<T> buildQuery(String queryString) throws Exception {
        QueryInfoBean bean = QueryBeanFactory.create(queryString);
        switch (bean.getQueryType()) {
            case DETAIL:
                return (Query<T>) DetailQueryBuilder.buildQuery((DetailQueryInfoBean) bean);
            case GROUP:
                return (Query<T>) GroupQueryBuilder.get().buildQuery((GroupQueryInfoBean) bean);
            case FUNNEL:
                return (Query<T>) FunnelQueryBuilder.buildQuery((FunnelQueryBean) bean);
            default:
                return Crasher.crash(new UnsupportedOperationException("unsupported Query type!"));
        }
    }

    public static <T extends QueryResultSet<?>> Query<T> buildPostQuery(final T mergeResultSet, QueryBean bean) {
        List<PostQueryInfo> postQueryInfoList = QueryInfoParser.parsePostQuery((QueryInfoBean) bean);
        Query<T> query;
        if (bean.getQueryType() == QueryType.GROUP) {
            query = (Query<T>) new UpdateNodeDataQuery((QueryResultSet<GroupPage>) mergeResultSet);
        } else {
            query = new Query<T>() {
                @Override
                public T getQueryResult() {
                    return mergeResultSet;
                }
            };
        }
        return PostQueryBuilder.buildQuery(query, postQueryInfoList);
    }
}
