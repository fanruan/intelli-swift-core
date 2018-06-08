package com.fr.swift.service;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.source.SwiftResultSet;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by pony on 2017/10/10.
 */
@RpcService(value = HistoryService.class, type = 1)
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {

    private static final long serialVersionUID = -6013675740141588108L;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryService.class);

    public static SwiftHistoryService getInstance() {
        return SingletonHolder.instance;
    }

    private SwiftHistoryService() {
    }

    @Override
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
    public <T extends SwiftResultSet> T query(QueryInfo<T> queryInfo) throws SQLException {
        return QueryBuilder.buildQuery(queryInfo).getQueryResult();
    }

    private static class SingletonHolder {
        private static final SwiftHistoryService instance = new SwiftHistoryService();
    }
}
