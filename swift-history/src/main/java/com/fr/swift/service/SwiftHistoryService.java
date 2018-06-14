package com.fr.swift.service;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.source.SerializableResultSet;
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
        // TODO: 2018/6/14 先到QueryResultSetManager找一下有没有缓存，没有则构建查询。
        // 另外分组表的resultSet在构建Query的时候处理好了，直接返回取出来的结果集即可。等明细部分好了一起改一下
        SwiftResultSet resultSet = QueryBuilder.buildQuery(queryInfo).getQueryResult();
        return new SerializableResultSet(resultSet);
    }

    private static class SingletonHolder {
        private static final SwiftHistoryService instance = new SwiftHistoryService();
    }
}
