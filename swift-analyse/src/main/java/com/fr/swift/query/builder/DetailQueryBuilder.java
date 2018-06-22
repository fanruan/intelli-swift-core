package com.fr.swift.query.builder;

import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.remote.RemoteQueryInfoImpl;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.remote.DetailLocalQuery;
import com.fr.swift.query.remote.RemoteQueryImpl;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
class DetailQueryBuilder {

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param info
     * @return
     */
    static Query<DetailResultSet> buildQuery(DetailQueryInfo info) throws SQLException {
        if (info.hasSort()){
            return buildQuery(info, LocalDetailQueryBuilder.GROUP);
        } else {
            return buildQuery(info, LocalDetailQueryBuilder.NORMAL);
        }
    }

    // TODO: 2018/6/20 目前看起来明细这边没必要区分localAll和localPart
    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的部分分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<DetailResultSet> buildLocalPartQuery(DetailQueryInfo info) {
        Query<DetailResultSet> query;
        if (info.hasSort()) {
            query = LocalDetailQueryBuilder.GROUP.buildLocalQuery(info);
        } else {
            query = LocalDetailQueryBuilder.NORMAL.buildLocalQuery(info);
        }
        return new DetailLocalQuery(info.getQueryId(), query);
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的全部分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<DetailResultSet> buildLocalAllQuery(DetailQueryInfo info) {
        Query<DetailResultSet> query;
        if (info.hasSort()) {
            query = LocalDetailQueryBuilder.GROUP.buildLocalQuery(info);
        } else {
            query = LocalDetailQueryBuilder.NORMAL.buildLocalQuery(info);
        }
        return new DetailLocalQuery(info.getQueryId(), query);
    }

    private static Query<DetailResultSet> buildQuery(DetailQueryInfo info, LocalDetailQueryBuilder builder) throws SQLException{
        SourceKey table = info.getTable();
        // TODO 这边先直接写成History
        List<SegmentDestination> uris = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        if (uris == null || uris.isEmpty()){
            throw new SwiftSegmentAbsentException("no such table");
        }
        if (isAllLocal(uris)) {
            return builder.buildLocalQuery(info);
        }
        // TODO: 2018/6/22 全部segment在一个远程节点上
//        if (uris.size() == 1) {
//            if (!uris.get(0).isRemote()) {
//
//            } else {
//                QueryInfo<DetailResultSet> queryInfo = new RemoteQueryInfoImpl<DetailResultSet>(QueryType.LOCAL_DETAIL, info);
//                return new RemoteQueryImpl<DetailResultSet>(queryInfo, uris.get(0));
//            }
//        }
        List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
        for (SegmentDestination uri : uris) {
            if (!uri.isRemote()) {
                queries.add(builder.buildLocalQuery(info));
            } else {
                QueryInfo<DetailResultSet> queryInfo = new RemoteQueryInfoImpl<DetailResultSet>(QueryType.LOCAL_DETAIL, info);
                queries.add(new RemoteQueryImpl<DetailResultSet>(queryInfo, uri));
            }
        }
        return builder.buildResultQuery(queries, info);
    }

    static boolean isAllLocal(List<SegmentDestination> uris) {
        for (SegmentDestination uri : uris) {
            if (uri.isRemote()) {
                return false;
            }
        }
        return true;
    }
}
