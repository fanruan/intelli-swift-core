package com.fr.swift.query.builder;

import com.fr.swift.query.Query;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryType;
import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.info.group.RemoteQueryInfo;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.sql.SQLException;

/**
 * Created by pony on 2017/12/12.
 */
public class QueryBuilder {
    public static <T extends SwiftResultSet> Query<T> buildQuery(QueryInfo<T> info) throws SQLException{
        switch (info.getType()) {
            case GROUP:
            case CROSS_GROUP:
                return (Query<T>) buildGroupQuery((GroupQueryInfoImpl) info);
            case RESULT_JOIN:
                return (Query<T>) buildResultJoinQuery((ResultJoinQueryInfo) info);
            case REMOTE_ALL:
                return buildQuery(((RemoteQueryInfo) info).getQueryInfo());
            case REMOTE_PART:
                return (Query<T>) buildLocalQuery(((RemoteQueryInfo) info).getQueryInfo());
            default:
                return (Query<T>) buildDetailQuery((DetailQueryInfo) info);
        }
    }

    private static Query<NodeResultSet> buildResultJoinQuery(ResultJoinQueryInfo info) throws SQLException {
        return ResultJoinQueryBuilder.buildQuery(info);
    }

    private static Query<NodeResultSet> buildGroupQuery(GroupQueryInfo info) {
        return GroupQueryBuilder.buildQuery(info);
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的部分分块数据
     *
     * @param info 查询信息
     * @return
     */
    private static <T extends SwiftResultSet> Query<T> buildLocalQuery(QueryInfo<T> info) throws SQLException {
        QueryType type = info.getType();
        if (type == QueryType.GROUP) {
            return (Query<T>) GroupQueryBuilder.buildLocalQuery((GroupQueryInfo) info);
        } else {
            return (Query<T>) DetailQueryBuilder.buildQuery((DetailQueryInfo) info);
        }
    }

    private static Query<DetailResultSet> buildDetailQuery(DetailQueryInfo info) throws SQLException{
        return DetailQueryBuilder.buildQuery(info);
    }

    protected static boolean isLocalURI(URI uri) {
        return true;
    }
}
