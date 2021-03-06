package com.fr.swift.cloud.query.builder;

import com.fr.swift.cloud.query.group.by2.node.GroupPage;
import com.fr.swift.cloud.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.cloud.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.cloud.query.info.bean.query.QueryInfoBean;
import com.fr.swift.cloud.query.info.group.post.PostQueryInfo;
import com.fr.swift.cloud.query.post.UpdateNodeDataQuery;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

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
        return buildQuery(bean);
    }


    public static <T extends QueryResultSet<?>> Query<T> buildQuery(QueryBean bean) throws Exception {
        switch (bean.getQueryType()) {
            case DETAIL:
                return (Query<T>) DetailQueryBuilder.of((DetailQueryInfoBean) bean).buildQuery();
            case GROUP:
                return (Query<T>) GroupQueryBuilder.get().buildQuery((GroupQueryInfoBean) bean);
            default:
                throw new IllegalArgumentException(String.format("unsupported Query type! %s", bean.getQueryType()));
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
