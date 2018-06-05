package com.fr.swift.service;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.source.SwiftResultSet;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Set;

/**
 * Created by pony on 2017/10/10.
 */
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {

    private static final long serialVersionUID = -6013675740141588108L;
    private final static SwiftHistoryService instance = new SwiftHistoryService();

    private SwiftHistoryService() {
    }

    public static SwiftHistoryService getInstance() {
        return instance;
    }



    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }

    @Override
    public <T extends SwiftResultSet> T query(QueryInfo<T> queryInfo) {
        SwiftLoggers.getLogger().info("History query");
        return null;
    }

    @Override
    public void load(Set<URI> remoteUris) throws IOException {

        SwiftLoggers.getLogger().info("History load uri");
        if (null != remoteUris && !remoteUris.isEmpty()) {
            String path = SwiftCubePathConfig.getInstance().getPath();
            SwiftRepository repository = SwiftRepositoryManager.getManager().getDefaultRepository();
            for (URI remote : remoteUris) {
                repository.copyFromRemote(remote, URI.create(path + remote.getPath()));
            }
        }
    }
}
