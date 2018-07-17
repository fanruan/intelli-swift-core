package com.fr.swift.query.builder;

import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanManager;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.remote.RemoteQueryImpl;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    static Query<DetailResultSet> buildQuery(DetailQueryInfo info) throws Exception {
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
        if (info.hasSort()) {
            return LocalDetailQueryBuilder.GROUP.buildLocalQuery(info);
        } else {
            return LocalDetailQueryBuilder.NORMAL.buildLocalQuery(info);
        }
    }

    /**
     * 处理另一个节点转发过来的查询，并且当前节点上包含查询的全部分块数据
     *
     * @param info 查询信息
     * @return
     */
    static Query<DetailResultSet> buildLocalAllQuery(DetailQueryInfo info) {
        if (info.hasSort()) {
            return LocalDetailQueryBuilder.GROUP.buildLocalQuery(info);
        } else {
            return LocalDetailQueryBuilder.NORMAL.buildLocalQuery(info);
        }
    }

    private static Query<DetailResultSet> buildQuery(DetailQueryInfo info, LocalDetailQueryBuilder builder) throws Exception {
        SourceKey table = info.getTable();
        List<SegmentDestination> uris = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        if (uris == null || uris.isEmpty()){
            throw new SwiftSegmentAbsentException("no such table");
        }
        if (isAllLocal(uris)) {
            return builder.buildLocalQuery(info);
        }
        List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
        Set<URI> localURIs = getLocalSegments(uris);
        if (!localURIs.isEmpty()) {
            info.setQuerySegment(localURIs);
            queries.add(builder.buildLocalQuery(info));
        }
        Map<String, List<SegmentDestination>> map = groupSegmentInfoByClusterId(uris);
        QueryBean queryBean = QueryBeanManager.getInstance().getQueryBean(info.getQueryId());
        for (Map.Entry<String, List<SegmentDestination>> entry : map.entrySet()) {
            queryBean.setQueryType(QueryType.LOCAL_DETAIL);
            SegmentDestination destination = entry.getValue().get(0);
            String newId = info.getQueryId() + destination.getClusterId();
            ((DetailQueryInfoBean) queryBean).setQueryId(newId);
            String jsonString = QueryInfoBeanFactory.queryBean2String(queryBean);
            queries.add(new RemoteQueryImpl<DetailResultSet>(jsonString, destination));
            QueryBeanManager.getInstance().put(newId, Pair.of(jsonString, destination));
        }
        return builder.buildResultQuery(queries, info);
    }

    static Set<URI> getLocalSegments(List<SegmentDestination> uris) {
        Set<URI> set = new HashSet<URI>();
        for (SegmentDestination destination : uris) {
            if (!destination.isRemote() && destination.getUri() != null) {
                set.add(destination.getUri());
            }
        }
        return set;
    }

    static Map<String, List<SegmentDestination>> groupSegmentInfoByClusterId(List<SegmentDestination> uris) {
        Map<String, List<SegmentDestination>> map = new HashMap<String, List<SegmentDestination>>();
        for (SegmentDestination destination : uris) {
            if (destination.isRemote()) {
                String clusterId = destination.getClusterId();
                if (!map.containsKey(clusterId)) {
                    map.put(clusterId, new ArrayList<SegmentDestination>());
                }
                map.get(clusterId).add(destination);
            }
        }
        return map;
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
