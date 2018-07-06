package com.fr.swift.service;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.session.AbstractSession;
import com.fr.swift.query.session.Session;
import com.fr.swift.query.session.SessionBuilder;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.RowDeleter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/10/10.
 */
@RpcService(value = HistoryService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {

    private static final long serialVersionUID = -6013675740141588108L;

    public static SwiftHistoryService getInstance() {
        return SingletonHolder.instance;
    }

    private transient SwiftSegmentManager segmentManager = (SwiftSegmentManager) SwiftContext.getInstance().getBean("localSegmentProvider");

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
            SwiftLoggers.getLogger(SwiftHistoryService.class).warn("Receive an empty URI set. Skip loading.");
        }
    }

    @Override
    @RpcMethod(methodName = "cleanMetaCache")
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.getInstance().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }


    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }

    @Override
    @RpcMethod(methodName = "historyQuery")
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = QueryInfoBeanFactory.create(queryDescription);
            SessionFactory factory = SwiftContext.getInstance().getBean(SessionFactory.class);
            return factory.openSession(new SessionBuilder() {
                @Override
                public Session build(long cacheTimeout) {
                    return new AbstractSession(cacheTimeout) {
                        @Override
                        protected SwiftResultSet query(QueryBean queryInfo) throws SQLException {
                            return QueryBuilder.buildQuery(queryInfo).getQueryResult();
                        }
                    };
                }

                @Override
                public String getQueryId() {
                    return bean.getQueryId();
                }
            }).executeQuery(bean);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    @RpcMethod(methodName = "historyDelete")
    public boolean delete(SourceKey sourceKey, Where where) throws Exception {
        try {
            List<Segment> segments = segmentManager.getSegment(sourceKey);
            for (Segment segment : segments) {
                RowDeleter rowDeleter = (RowDeleter) SwiftContext.getInstance().getBean("decrementer", segment);
                rowDeleter.delete(sourceKey, where);
            }
            //todo upload allshowindex;
            return true;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    private static class SingletonHolder {
        private static final SwiftHistoryService instance = new SwiftHistoryService();
    }
}
