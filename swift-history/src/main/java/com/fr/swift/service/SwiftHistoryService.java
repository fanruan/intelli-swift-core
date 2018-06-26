package com.fr.swift.service;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.session.AbstractSession;
import com.fr.swift.query.session.Session;
import com.fr.swift.query.session.SessionBuilder;
import com.fr.swift.query.session.factory.SessionFactory;
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
            SwiftRepository repository = SwiftRepositoryManager.getManager().getCurrentRepository();
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
    @SuppressWarnings("Duplicates")
    @RpcMethod(methodName = "historyQuery")
    public SwiftResultSet query(final QueryBean queryInfo) throws SQLException {
        // TODO: 2018/6/14 先到QueryResultSetManager找一下有没有缓存，没有则构建查询。
        // 另外分组表的resultSet在构建Query的时候处理好了，直接返回取出来的结果集即可。等明细部分好了一起改一下
        SessionFactory factory = SwiftContext.getInstance().getBean(SessionFactory.class);
        return factory.openSession(new SessionBuilder() {
            @Override
            public Session build(long cacheTimeout) {
                return new AbstractSession(cacheTimeout) {
                    @Override
                    protected SwiftResultSet query(QueryBean queryInfo) throws SQLException {
                        // TODO: 2018/6/20 @yee 先到QueryResultSetManager找一下有没有缓存，没有则构建查询
                        SwiftResultSet resultSet = QueryBuilder.buildQuery(queryInfo).getQueryResult();
                        SerializableResultSet result;
                        QueryType type = queryInfo.getQueryType();
                        switch (type) {
                            case LOCAL_GROUP_ALL:
                                result = new LocalAllNodeResultSet(queryInfo.getQueryId(), (NodeResultSet<SwiftNode>) resultSet);
                                break;
                            case LOCAL_GROUP_PART:
                                result = new LocalPartNodeResultSet(queryInfo.getQueryId(), (NodeMergeResultSet<GroupNode>) resultSet);
                                break;
                            default:
                                result = new SerializableDetailResultSet(queryInfo.getQueryId(), (DetailResultSet) resultSet);
                        }
                        return result;
                    }
                };
            }

            @Override
            public String getQueryId() {
                return queryInfo.getQueryId();
            }
        }).executeQuery(queryInfo);
    }

    private static class SingletonHolder {
        private static final SwiftHistoryService instance = new SwiftHistoryService();
    }
}
