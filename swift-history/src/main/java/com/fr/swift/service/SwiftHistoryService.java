package com.fr.swift.service;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryResultSetManager;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.serialize.LocalAllNodeResultSet;
import com.fr.swift.result.serialize.LocalPartNodeResultSet;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.result.serialize.SerializableResultSet;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.source.SwiftResultSet;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by pony on 2017/10/10.
 */
@RpcService(value = HistoryService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {

    private static final long serialVersionUID = -6013675740141588108L;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryService.class);

    public static SwiftHistoryService getInstance() {
        return SingletonHolder.instance;
    }

    private SwiftHistoryService() {
    }

    @Override
    @RpcMethod(methodName = "load")
    public void load(Set<URI> remoteUris) throws IOException {
        if (null != remoteUris && !remoteUris.isEmpty()) {
            String path = SwiftCubePathConfig.getInstance().getPath();
            SwiftRepository repository = SwiftRepositoryManager.getManager().getDefaultRepository();
            for (URI remote : remoteUris) {
                repository.copyFromRemote(remote, URI.create(path + remote.getPath()));
            }
        } else {
            LOGGER.warn("Receive an empty URI set. Skip loading.");
        }
    }


    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }

    @Override
    @RpcMethod(methodName = "historyQuery")
    public SerializableResultSet query(QueryInfo queryInfo) throws SQLException {
        // 先到QueryResultSetManager找一下有没有缓存，没有则构建查询。
        SwiftResultSet resultSet = QueryResultSetManager.getInstance().get(queryInfo.getQueryId());
        if (resultSet == null) {
            resultSet = QueryBuilder.buildQuery(queryInfo).getQueryResult();
        }
        QueryType type = queryInfo.getType();
        switch (type) {
            case LOCAL_GROUP_ALL:
                return new LocalAllNodeResultSet(queryInfo.getQueryId(), (NodeResultSet<SwiftNode>) resultSet);
            case LOCAL_GROUP_PART:
                return new LocalPartNodeResultSet(queryInfo.getQueryId(), (NodeMergeResultSet<GroupNode>) resultSet);
            default:
                return new SerializableDetailResultSet(queryInfo.getQueryId(), (DetailResultSet) resultSet);
        }
    }

    private static class SingletonHolder {
        private static final SwiftHistoryService instance = new SwiftHistoryService();
    }
}
