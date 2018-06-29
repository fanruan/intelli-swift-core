package com.fr.swift.query.builder;

import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanManager;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/12.
 */
public final class QueryBuilder {

    /**
     * 外部调用的查询接口都传QueryBean，QueryInfo和处理QueryInfo的QueryBuilder都作为内部实现
     *
     * @param bean
     * @param <T>
     * @return
     * @throws SQLException
     */
    public static <T extends SwiftResultSet> Query<T> buildQuery(QueryBean bean) throws SQLException {
        // 缓存一下queryBean，省的后面queryBean和queryInfo传来传去
        QueryBeanManager.getInstance().put(bean.getQueryId(), bean);
        QueryInfoBean infoBean = (QueryInfoBean) bean;
        QueryInfo info = QueryInfoParser.parse(infoBean);
        switch (info.getType()) {
            case LOCAL_GROUP_ALL:
            case LOCAL_GROUP_PART:
            case LOCAL_DETAIL:
                return buildLocalQuery(info);
            default:
                return buildQuery(info);
        }
    }

    /**
     * 非LOCAL_*类型，被查询服务调用，构建过程中依赖查询服务提供的segment分布信息
     *
     * @param info
     * @param <T>
     * @return
     * @throws SQLException
     */
    static <T extends SwiftResultSet> Query<T> buildQuery(QueryInfo<T> info) throws SQLException {
        switch (info.getType()) {
            case GROUP:
            case CROSS_GROUP:
                return (Query<T>) buildGroupQuery((GroupQueryInfoImpl) info);
            case RESULT_JOIN:
                return (Query<T>) buildResultJoinQuery((ResultJoinQueryInfo) info);
            default:
                return (Query<T>) buildDetailQuery((DetailQueryInfo) info);
        }
    }

    /**
     * LOCAL_*类型，被history或者realTime节点调用，仅构建本地query，不依赖查询服务
     *
     * @param info
     * @param <T>
     * @return
     */
    static <T extends SwiftResultSet> Query<T> buildLocalQuery(QueryInfo<T> info) {
        switch (info.getType()) {
            case LOCAL_DETAIL:
            case LOCAL_GROUP_ALL:
                return buildLocalAllQuery(info);
            default:
                return buildLocalPartQuery(info);
        }
    }

    private static Query<NodeResultSet> buildResultJoinQuery(ResultJoinQueryInfo info) throws SQLException {
        return ResultJoinQueryBuilder.buildQuery(info);
    }

    private static Query<NodeResultSet> buildGroupQuery(GroupQueryInfo info) {
        return GroupQueryBuilder.buildQuery(info);
    }

    private static Query<DetailResultSet> buildDetailQuery(DetailQueryInfo info) throws SQLException {
        return DetailQueryBuilder.buildQuery(info);
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的部分分块数据
     *
     * @param info 查询信息
     * @return
     */
    private static <T extends SwiftResultSet> Query<T> buildLocalPartQuery(QueryInfo<T> info) {
        QueryType type = info.getType();
        if (type == QueryType.LOCAL_GROUP_PART) {
            return (Query<T>) GroupQueryBuilder.buildLocalPartQuery((GroupQueryInfo) info);
        } else {
            return (Query<T>) DetailQueryBuilder.buildLocalPartQuery((DetailQueryInfo) info);
        }
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的全部分块数据
     *
     * @param info 查询信息
     * @return
     */
    private static <T extends SwiftResultSet> Query<T> buildLocalAllQuery(QueryInfo<T> info) {
        QueryType type = info.getType();
        if (type == QueryType.LOCAL_GROUP_ALL) {
            return (Query<T>) GroupQueryBuilder.buildLocalAllQuery((GroupQueryInfo) info);
        } else {
            return (Query<T>) DetailQueryBuilder.buildLocalAllQuery((DetailQueryInfo) info);
        }
    }
}
